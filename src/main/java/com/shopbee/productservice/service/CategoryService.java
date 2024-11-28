package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.CategoryCreationRequest;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.PagedResponse;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Category;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.mapper.CategoryMapper;
import com.shopbee.productservice.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
@Transactional
public class CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public PagedResponse<Category> getByCriteria(@Valid SortCriteria sortCriteria, @Valid PageRequest pageRequest) {
        List<Category> categories = categoryRepository.findByCriteria(sortCriteria, pageRequest);
        Long totalCategories = categoryRepository.count();
        return PagedResponse.from(totalCategories, pageRequest, categories);
    }

    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductServiceException("Category not found", Response.Status.NOT_FOUND));
    }

    public Category getById(Long id) {
        return categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProductServiceException("Category not found", Response.Status.NOT_FOUND));
    }

    public Category create(@Valid CategoryCreationRequest categoryCreationRequest) {
        if (categoryRepository.existBySlug(categoryCreationRequest.getSlug())) {
            throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
        }

        Category category = categoryMapper.toCategory(categoryCreationRequest);
        categoryRepository.persist(category);
        return category;
    }

    public void update(Long id, CategoryCreationRequest categoryCreationRequest) {
        categoryRepository.findBySlug(categoryCreationRequest.getSlug()).ifPresent(brand -> {
            if (!id.equals(brand.getId())) {
                throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
            }
        });

        Category category = getById(id);
        category.setName(categoryCreationRequest.getName());
        category.setSlug(categoryCreationRequest.getSlug());
        category.setDescription(categoryCreationRequest.getDescription());
    }

    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    public void delete(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }
}
