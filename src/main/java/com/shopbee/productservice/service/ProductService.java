package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.*;
import com.shopbee.productservice.entity.Brand;
import com.shopbee.productservice.entity.Category;
import com.shopbee.productservice.entity.Model;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.mapper.ProductMapper;
import com.shopbee.productservice.repository.BrandRepository;
import com.shopbee.productservice.repository.CategoryRepository;
import com.shopbee.productservice.repository.ModelRepository;
import com.shopbee.productservice.repository.ProductRepository;
import com.shopbee.productservice.shared.converter.impl.ProductConverter;
import com.shopbee.productservice.shared.external.cart.Cart;
import com.shopbee.productservice.shared.external.cart.CartServiceClient;
import com.shopbee.productservice.shared.external.recommendation.Behavior;
import com.shopbee.productservice.shared.external.recommendation.GetEvaluateResponse;
import com.shopbee.productservice.shared.external.recommendation.GetRecommendedProductsRequest;
import com.shopbee.productservice.shared.external.recommendation.RecommendationServiceClient;
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
import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.*;

/**
 * The type Product service.
 */
@Slf4j
@ApplicationScoped
@Transactional
public class ProductService {

    private final ProductConverter productConverter;
    private final ReviewServiceClient reviewServiceClient;
    private final CartServiceClient cartServiceClient;
    private final RecommendationServiceClient recommendationServiceClient;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final CategoryRepository categoryRepository;
    private final ModelService modelService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserService userService;
    private final SecurityIdentity identity;

    /**
     * Instantiates a new Product service.
     *
     * @param productConverter    the product converter
     * @param reviewServiceClient the review service client
     * @param brandRepository     the brand repository
     * @param modelRepository     the model repository
     * @param categoryRepository  the category repository
     * @param modelService        the model service
     * @param categoryService     the category service
     * @param productRepository   the product repository
     * @param productMapper       the product mapper
     * @param userService         the user service
     * @param identity            the identity
     */
    public ProductService(ProductConverter productConverter,
                          @RestClient ReviewServiceClient reviewServiceClient,
                          @RestClient CartServiceClient cartServiceClient,
                          @RestClient RecommendationServiceClient recommendationServiceClient,
                          BrandRepository brandRepository,
                          ModelRepository modelRepository,
                          CategoryRepository categoryRepository,
                          ModelService modelService,
                          CategoryService categoryService,
                          ProductRepository productRepository,
                          ProductMapper productMapper,
                          UserService userService,
                          SecurityIdentity identity) {
        this.productConverter = productConverter;
        this.reviewServiceClient = reviewServiceClient;
        this.cartServiceClient = cartServiceClient;
        this.recommendationServiceClient = recommendationServiceClient;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.categoryRepository = categoryRepository;
        this.modelService = modelService;
        this.categoryService = categoryService;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.userService = userService;
        this.identity = identity;
    }

    /**
     * Gets recommendations.
     *
     * @return the recommendations
     */
    public List<ProductResponse> getRecommendations() {
        List<Product> activeProducts = getAllActive();

        List<Long> inCarts = getInCartProductIds();
        Behavior behavior = Behavior.builder()
                .favourites(Collections.emptyList())
                .inCart(inCarts)
                .recentVisits(Collections.emptyList())
                .build();
        GetRecommendedProductsRequest getRecommendedProductsRequest =
                GetRecommendedProductsRequest.builder()
                        .behavior(behavior)
                        .availableProducts(activeProducts)
                        .build();
        GetEvaluateResponse evaluateResponse = recommendationServiceClient.evaluate(getRecommendedProductsRequest);

        return evaluateResponse.getRecommendations();
    }

    /**
     * Gets all active.
     *
     * @return the all active
     */
    public List<Product> getAllActive() {
        FilterCriteria activeFilter = FilterCriteria.builder().active(true).build();
        return productRepository.findByCriteria(activeFilter);
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
        return toProductResponse(product);
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
     * Gets product statistic.
     *
     * @return the product statistic
     */
    public ProductStatistic getProductStatistic() {
        long totalProducts = productRepository.count();
        FilterCriteria activeFilter = new FilterCriteria();
        activeFilter.setActive(true);
        long totalActiveProducts = productRepository.count(activeFilter);

        List<Brand> brands = brandRepository.listAll();
        Map<String, Long> productsByBrand = new HashedMap<>();
        brands.forEach(brand -> {
            FilterCriteria brandFilter = FilterCriteria.builder().brands(List.of(brand.getSlug())).build();
            long totalByBrand = productRepository.count(brandFilter);
            productsByBrand.put(brand.getSlug(), totalByBrand);
        });

        List<Model> models = modelRepository.listAll();
        Map<String, Long> productsByModel = new HashedMap<>();
        models.forEach(model -> {
            FilterCriteria modelFilter = FilterCriteria.builder().models(List.of(model.getSlug())).build();
            long totalByModel = productRepository.count(modelFilter);
            productsByModel.put(model.getSlug(), totalByModel);
        });

        List<Category> categories = categoryRepository.listAll();
        Map<String, Long> productsByCategory = new HashedMap<>();
        categories.forEach(category -> {
            FilterCriteria categoryFilter = FilterCriteria.builder().categories(List.of(category.getSlug())).build();
            long totalByCategory = productRepository.count(categoryFilter);
            productsByCategory.put(category.getSlug(), totalByCategory);
        });

        return ProductStatistic.builder()
                .totalProducts(totalProducts)
                .totalActiveProducts(totalActiveProducts)
                .totalProductsByBrand(productsByBrand)
                .totalProductsByModel(productsByModel)
                .totalProductsByCategory(productsByCategory)
                .build();
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

    private List<Long> getInCartProductIds() {
        PageRequest pageRequest = PageRequest.builder().size(Integer.MAX_VALUE).build();

        PagedResponse<Cart> cartPagedResponse = cartServiceClient.getCurrent(pageRequest);
        List<Cart> carts = Optional.ofNullable(cartPagedResponse).map(PagedResponse::getItems).orElse(Collections.emptyList());
        List<String> productSlug = carts.stream().map(Cart::getProductSlug).toList();
        List<Product> products = productSlug.stream().map(slug -> productRepository.findBySlug(slug).orElse(null)).toList();

        return products.stream().filter(Objects::nonNull).map(Product::getId).toList();
    }

    /**
     * Convert from product product response.
     *
     * @param product the product
     * @return the product response
     */
    private ProductResponse toProductResponse(Product product) {
        if (Objects.isNull(product)) {
            return null;
        }

        ProductResponse response = productConverter.convert(product);
        try {
            ReviewStatistic statistic = reviewServiceClient.getStatistic(product.getSlug());
            response.setRating(statistic.getAverageRating());
        } catch (Exception e) {
            log.warn("Failed to get rating.");
        }
        return response;
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
