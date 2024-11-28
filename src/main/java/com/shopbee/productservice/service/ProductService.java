package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.*;
import com.shopbee.productservice.entity.Category;
import com.shopbee.productservice.entity.Model;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.mapper.ProductMapper;
import com.shopbee.productservice.repository.ProductRepository;
import com.shopbee.productservice.shared.converter.impl.ProductConverter;
import com.shopbee.productservice.shared.external.review.ReviewServiceClient;
import com.shopbee.productservice.shared.external.review.ReviewStatistic;
import com.shopbee.productservice.shared.external.user.User;
import com.shopbee.productservice.shared.external.user.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The type Product service.
 */
@Slf4j
@ApplicationScoped
@Transactional
public class ProductService {

    private final ProductConverter productConverter;
    private final ReviewServiceClient reviewServiceClient;
    ModelService modelService;
    CategoryService categoryService;
    ProductRepository productRepository;
    ProductMapper productMapper;
    UserService userService;
    SecurityIdentity identity;

    /**
     * Instantiates a new Product service.
     *
     * @param modelService      the model service
     * @param categoryService   the category service
     * @param productRepository the product repository
     * @param productMapper     the product mapper
     * @param userService       the user service
     * @param identity          the identity
     */
    public ProductService(ProductConverter productConverter,
                          @RestClient ReviewServiceClient reviewServiceClient,
                          ModelService modelService,
                          CategoryService categoryService,
                          ProductRepository productRepository,
                          ProductMapper productMapper,
                          UserService userService,
                          SecurityIdentity identity) {
        this.productConverter = productConverter;
        this.reviewServiceClient = reviewServiceClient;
        this.modelService = modelService;
        this.categoryService = categoryService;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.userService = userService;
        this.identity = identity;
    }

    /**
     * Gets by criteria.
     *
     * @param filterCriteria the filter criteria
     * @param sortCriteria   the sort criteria
     * @param pageRequest    the page request
     * @return the by criteria
     */
    public PagedResponse<Product> getByCriteria(@Valid FilterCriteria filterCriteria,
                                                @Valid SortCriteria sortCriteria,
                                                @Valid PageRequest pageRequest) {
        if (Optional.ofNullable(filterCriteria.getBrandListString()).isPresent()) {
            List<String> brands = List.of(filterCriteria.getBrandListString().split(","));
            filterCriteria.setBrands(brands);
        }
        if (Optional.ofNullable(filterCriteria.getCategoryListString()).isPresent()) {
            List<String> categories = List.of(filterCriteria.getCategoryListString().split(","));
            filterCriteria.setCategories(categories);
        }

        List<Product> pagedProducts =
                productRepository.findByCriteria(filterCriteria, sortCriteria, pageRequest);
        long totalProducts = productRepository.count(filterCriteria);
        return PagedResponse.from(totalProducts, pageRequest, pagedProducts);
    }

    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    public Product getById(Long id) {
        return productRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProductServiceException("Product not found", Response.Status.NOT_FOUND));
    }

    /**
     * Gets by slug.
     *
     * @param slug the slug
     * @return the by slug
     */
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductServiceException("Product not found", Response.Status.NOT_FOUND));
        ProductResponse response = productConverter.convert(product);
        try {
            ReviewStatistic statistic = reviewServiceClient.getStatistic(slug);
            response.setRating(statistic.getAverageRating());
        } catch (Exception e) {
            log.warn("Failed to get rating.");
        }
        return response;
    }

    /**
     * Increase view.
     *
     * @param slug the slug
     */
    public void increaseView(String slug) {
        Product product = productRepository.findBySlug(slug).orElseThrow(() -> new ProductServiceException("Product not found.", Response.Status.NOT_FOUND));
        increaseView(product);
    }

    /**
     * Create product.
     *
     * @param productCreationRequest the product creation request
     * @return the product
     */
    public Product create(@Valid ProductCreationRequest productCreationRequest) {
        productRepository.findBySlug(productCreationRequest.getSlug())
                .ifPresent((product) -> {
                    throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
                });
        validatePrice(productCreationRequest);
        normalizePrice(productCreationRequest);

        User user = userService.getByUsername(identity.getPrincipal().getName());
        Product product = productMapper.toProduct(productCreationRequest);
        Model model = modelService.getBySlug(productCreationRequest.getModel());
        Category category = categoryService.getBySlug(productCreationRequest.getCategory());

        product.setUserId(user.getId());
        product.setModel(model);
        product.setCategory(category);

        productRepository.persist(product);
        return product;
    }

    /**
     * Update.
     *
     * @param id                     the id
     * @param productCreationRequest the product creattion request
     */
    public void update(Long id, @Valid ProductCreationRequest productCreationRequest) {
        productRepository.findBySlug(productCreationRequest.getSlug())
                .ifPresent((product) -> validateProductSlug(id, product));

        validatePrice(productCreationRequest);
        normalizePrice(productCreationRequest);

        Model model = modelService.getBySlug(productCreationRequest.getModel());
        Category category = categoryService.getBySlug(productCreationRequest.getCategory());

        Product product = getById(id);
        product.setName(productCreationRequest.getName());
        product.setSlug(productCreationRequest.getSlug());
        product.setDescription(productCreationRequest.getDescription());
        product.setBasePrice(productCreationRequest.getBasePrice());
        product.setSalePrice(productCreationRequest.getSalePrice());
        product.setStockQuantity(productCreationRequest.getStockQuantity());
        product.setActive(productCreationRequest.isActive());
        product.setWeight(productCreationRequest.getWeight());
        product.setColor(ProductMapper.toColor(productCreationRequest.getColor()));
        product.setProcessor(productCreationRequest.getProcessor());
        product.setGpu(productCreationRequest.getGpu());
        product.setRam(productCreationRequest.getRam());
        product.setStorageType(ProductMapper.toStorageType(productCreationRequest.getStorageType()));
        product.setOs(ProductMapper.toOS(productCreationRequest.getOs()));
        product.setScreenSize(productCreationRequest.getScreenSize());
        product.setBatteryCapacity(productCreationRequest.getBatteryCapacity());
        product.setWarranty(productCreationRequest.getWarranty());
        product.setModel(model);
        product.setCategory(category);
    }

    /**
     * Update quantity.
     *
     * @param productQuantityRequest the product quantity request
     */
    public void updateQuantity(@Valid ProductQuantityRequest productQuantityRequest) {
        Product product = getById(productQuantityRequest.getId());
        product.setStockQuantity(productQuantityRequest.getQuantity());
    }

    /**
     * Delete.
     *
     * @param ids the ids
     */
    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * Delete.
     *
     * @param id the id
     */
    private void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    /**
     * Increase view.
     *
     * @param product the product
     */
    private void increaseView(Product product) {
        long viewCount = product.getViewCount();
        product.setViewCount(++viewCount);
    }

    /**
     * Validate product slug.
     *
     * @param id      the id
     * @param product the product
     */
    private void validateProductSlug(Long id, Product product) {
        if (!id.equals(product.getId())) {
            throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
        }
    }

    /**
     * Validate price.
     *
     * @param productCreationRequest the product creattion request
     */
    private void validatePrice(ProductCreationRequest productCreationRequest) {
        if (productCreationRequest.getSalePrice().compareTo(productCreationRequest.getBasePrice()) > 0) {
            throw new ProductServiceException("Sale price must less or equal base price", Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Normalize price.
     *
     * @param productCreationRequest the product creattion request
     */
    private void normalizePrice(ProductCreationRequest productCreationRequest) {
        if (productCreationRequest.getSalePrice() == null || productCreationRequest.getSalePrice().equals(BigDecimal.ZERO)) {
            productCreationRequest.setSalePrice(productCreationRequest.getBasePrice());
        }
    }
}
