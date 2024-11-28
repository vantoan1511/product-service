package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.BrandCreationRequest;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.PagedResponse;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Brand;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.mapper.BrandMapper;
import com.shopbee.productservice.repository.BrandRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
@Transactional
public class BrandService {

    BrandRepository brandRepository;

    BrandMapper brandMapper;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    public PagedResponse<Brand> getByCriteria(@Valid SortCriteria sortCriteria, @Valid PageRequest pageRequest) {
        List<Brand> brands = brandRepository.findByCriteria(sortCriteria, pageRequest);
        Long totalBrands = brandRepository.count();
        return PagedResponse.from(totalBrands, pageRequest, brands);
    }

    public Brand getBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductServiceException("Brand not found: " + slug, Response.Status.NOT_FOUND));
    }

    public Brand getById(Long id) {
        return brandRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProductServiceException("Brand not found: " + id, Response.Status.NOT_FOUND));
    }

    public Brand create(@Valid BrandCreationRequest brandCreationRequest) {
        if (brandRepository.existBySlug(brandCreationRequest.getSlug())) {
            throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
        }

        Brand brand = brandMapper.toBrand(brandCreationRequest);
        brandRepository.persist(brand);
        return brand;
    }

    public void update(Long id, BrandCreationRequest brandUpdate) {
        brandRepository.findBySlug(brandUpdate.getSlug()).ifPresent(brand -> {
            if (!id.equals(brand.getId())) {
                throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
            }
        });

        Brand brand = getById(id);
        brand.setName(brandUpdate.getName());
        brand.setSlug(brandUpdate.getSlug());
        brand.setDescription(brandUpdate.getDescription());
    }

    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    public void delete(Long id) {
        Brand brand = getById(id);
        brandRepository.delete(brand);
    }

}
