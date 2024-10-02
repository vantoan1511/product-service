package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.PagedResponse;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class ProductService {

    ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public PagedResponse<Product> getByCriteria(@Valid PageRequest pageRequest) {
        List<Product> pagedProducts =
                productRepository.findByCriteria(pageRequest.getPage() - 1, pageRequest.getSize());
        return new PagedResponse<Product>(productRepository.countAllProducts(),
                (long) pagedProducts.size(), pageRequest.getPage(), pageRequest.getSize(), pagedProducts);
    }
}
