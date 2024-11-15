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

@ApplicationScoped
@Transactional
public class ProductService {

    ModelService modelService;
    CategoryService categoryService;
    ProductRepository productRepository;
    ProductMapper productMapper;
    UserService userService;
    SecurityIdentity identity;

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

    public Product getById(Long id) {
        return productRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProductServiceException("Product not found", Response.Status.NOT_FOUND));
    }

    public Product getBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductServiceException("Product not found", Response.Status.NOT_FOUND));
    }

    public void increaseView(String slug) {
        Product product = getBySlug(slug);
        increaseView(product);
    }

    public Product create(@Valid ProductRequest productRequest) {
        productRepository.findBySlug(productRequest.getSlug())
                .ifPresent((product) -> {
                    throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
                });
        validatePrice(productRequest);
        normalizePrice(productRequest);

        User user = userService.getByUsername(identity.getPrincipal().getName());
        Product product = productMapper.toProduct(productRequest);
        Model model = modelService.getBySlug(productRequest.getModel());
        Category category = categoryService.getBySlug(productRequest.getCategory());

        product.setUserId(user.getId());
        product.setModel(model);
        product.setCategory(category);

        productRepository.persist(product);
        return product;
    }

    public void update(Long id, @Valid ProductRequest productRequest) {
        productRepository.findBySlug(productRequest.getSlug())
                .ifPresent((product) -> validateProductSlug(id, product));

        validatePrice(productRequest);
        normalizePrice(productRequest);

        Model model = modelService.getBySlug(productRequest.getModel());
        Category category = categoryService.getBySlug(productRequest.getCategory());

        Product product = getById(id);
        product.setName(productRequest.getName());
        product.setSlug(productRequest.getSlug());
        product.setDescription(productRequest.getDescription());
        product.setBasePrice(productRequest.getBasePrice());
        product.setSalePrice(productRequest.getSalePrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setActive(productRequest.isActive());
        product.setWeight(productRequest.getWeight());
        product.setColor(ProductMapper.toColor(productRequest.getColor()));
        product.setProcessor(productRequest.getProcessor());
        product.setGpu(productRequest.getGpu());
        product.setRam(productRequest.getRam());
        product.setStorageType(ProductMapper.toStorageType(productRequest.getStorageType()));
        product.setOs(ProductMapper.toOS(productRequest.getOs()));
        product.setScreenSize(productRequest.getScreenSize());
        product.setBatteryCapacity(productRequest.getBatteryCapacity());
        product.setWarranty(productRequest.getWarranty());
        product.setModel(model);
        product.setCategory(category);
    }

    public void updateQuantity(@Valid ProductQuantityRequest productQuantityRequest) {
        Product product = getById(productQuantityRequest.getId());
        product.setStockQuantity(productQuantityRequest.getQuantity());
    }

    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    private void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    private void increaseView(Product product) {
        long viewCount = product.getViewCount();
        product.setViewCount(++viewCount);
    }

    private void validateProductSlug(Long id, Product product) {
        if (!id.equals(product.getId())) {
            throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
        }
    }

    private void validatePrice(ProductRequest productRequest) {
        if (productRequest.getSalePrice().compareTo(productRequest.getBasePrice()) > 0) {
            throw new ProductServiceException("Sale price must less or equal base price", Response.Status.BAD_REQUEST);
        }
    }

    private void normalizePrice(ProductRequest productRequest) {
        if (productRequest.getSalePrice() == null || productRequest.getSalePrice().equals(BigDecimal.ZERO)) {
            productRequest.setSalePrice(productRequest.getBasePrice());
        }
    }
}
