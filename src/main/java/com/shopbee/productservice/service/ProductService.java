package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.*;
import com.shopbee.productservice.entity.Category;
import com.shopbee.productservice.entity.Model;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.mapper.ProductMapper;
import com.shopbee.productservice.repository.ProductRepository;
import com.shopbee.productservice.service.user.User;
import com.shopbee.productservice.service.user.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The type Product service.
 */
@ApplicationScoped
@Transactional
public class ProductService {

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
    public ProductService(ModelService modelService,
                          CategoryService categoryService,
                          ProductRepository productRepository,
                          ProductMapper productMapper,
                          UserService userService,
                          SecurityIdentity identity) {
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
    public Product getBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductServiceException("Product not found", Response.Status.NOT_FOUND));
    }

    /**
     * Increase view.
     *
     * @param slug the slug
     */
    public void increaseView(String slug) {
        Product product = getBySlug(slug);
        increaseView(product);
    }

    /**
     * Create product.
     *
     * @param productCreattionRequest the product creattion request
     * @return the product
     */
    public Product create(@Valid ProductCreattionRequest productCreattionRequest) {
        productRepository.findBySlug(productCreattionRequest.getSlug())
                .ifPresent((product) -> {
                    throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
                });
        validatePrice(productCreattionRequest);
        normalizePrice(productCreattionRequest);

        User user = userService.getByUsername(identity.getPrincipal().getName());
        Product product = productMapper.toProduct(productCreattionRequest);
        Model model = modelService.getBySlug(productCreattionRequest.getModel());
        Category category = categoryService.getBySlug(productCreattionRequest.getCategory());

        product.setUserId(user.getId());
        product.setModel(model);
        product.setCategory(category);

        productRepository.persist(product);
        return product;
    }

    /**
     * Update.
     *
     * @param id                      the id
     * @param productCreattionRequest the product creattion request
     */
    public void update(Long id, @Valid ProductCreattionRequest productCreattionRequest) {
        productRepository.findBySlug(productCreattionRequest.getSlug())
                .ifPresent((product) -> validateProductSlug(id, product));

        validatePrice(productCreattionRequest);
        normalizePrice(productCreattionRequest);

        Model model = modelService.getBySlug(productCreattionRequest.getModel());
        Category category = categoryService.getBySlug(productCreattionRequest.getCategory());

        Product product = getById(id);
        product.setName(productCreattionRequest.getName());
        product.setSlug(productCreattionRequest.getSlug());
        product.setDescription(productCreattionRequest.getDescription());
        product.setBasePrice(productCreattionRequest.getBasePrice());
        product.setSalePrice(productCreattionRequest.getSalePrice());
        product.setStockQuantity(productCreattionRequest.getStockQuantity());
        product.setActive(productCreattionRequest.isActive());
        product.setWeight(productCreattionRequest.getWeight());
        product.setColor(ProductMapper.toColor(productCreattionRequest.getColor()));
        product.setProcessor(productCreattionRequest.getProcessor());
        product.setGpu(productCreattionRequest.getGpu());
        product.setRam(productCreattionRequest.getRam());
        product.setStorageType(ProductMapper.toStorageType(productCreattionRequest.getStorageType()));
        product.setOs(ProductMapper.toOS(productCreattionRequest.getOs()));
        product.setScreenSize(productCreattionRequest.getScreenSize());
        product.setBatteryCapacity(productCreattionRequest.getBatteryCapacity());
        product.setWarranty(productCreattionRequest.getWarranty());
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
     * @param productCreattionRequest the product creattion request
     */
    private void validatePrice(ProductCreattionRequest productCreattionRequest) {
        if (productCreattionRequest.getSalePrice().compareTo(productCreattionRequest.getBasePrice()) > 0) {
            throw new ProductServiceException("Sale price must less or equal base price", Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Normalize price.
     *
     * @param productCreattionRequest the product creattion request
     */
    private void normalizePrice(ProductCreattionRequest productCreattionRequest) {
        if (productCreattionRequest.getSalePrice() == null || productCreattionRequest.getSalePrice().equals(BigDecimal.ZERO)) {
            productCreattionRequest.setSalePrice(productCreattionRequest.getBasePrice());
        }
    }
}
