package com.shopbee.productservice.service;

import com.shopbee.productservice.dto.ModelRequest;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.PagedResponse;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Brand;
import com.shopbee.productservice.entity.Model;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.mapper.ModelMapper;
import com.shopbee.productservice.repository.ModelRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;

@ApplicationScoped
@Transactional
public class ModelService {

    ModelRepository modelRepository;

    BrandService brandService;

    ModelMapper modelMapper;

    public ModelService(ModelRepository modelRepository,
                        BrandService brandService,
                        ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.brandService = brandService;
        this.modelMapper = modelMapper;
    }

    public PagedResponse<Model> getByCriteria(@Valid SortCriteria sortCriteria, @Valid PageRequest pageRequest) {
        List<Model> pagedModels = modelRepository.findByCriteria(sortCriteria, pageRequest);
        Long totalItems = modelRepository.count();
        return PagedResponse.from(totalItems, pageRequest, pagedModels);
    }

    public Model getBySlug(String slug) {
        return modelRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductServiceException("Model not found: " + slug, Response.Status.NOT_FOUND));
    }

    public Model create(@Valid ModelRequest modelRequest) {
        modelRepository.findBySlug(modelRequest.getSlug()).ifPresent((model) -> {
            throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
        });

        Brand brand = brandService.getBySlug(modelRequest.getBrandSlug());
        Model model = modelMapper.toModel(modelRequest);
        model.setBrand(brand);
        modelRepository.persist(model);
        return model;
    }

    public void update(Long id, ModelRequest modelUpdate) {
        modelRepository.findBySlug(modelUpdate.getSlug()).ifPresent(model -> {
            if (!id.equals(model.getId())) {
                throw new ProductServiceException("Slug existed", Response.Status.CONFLICT);
            }
        });

        Brand brand = brandService.getBySlug(modelUpdate.getBrandSlug());

        Model model = getById(id);
        model.setName(modelUpdate.getName());
        model.setSlug(modelUpdate.getSlug());
        model.setDescription(modelUpdate.getDescription());
        model.setBrand(brand);
    }

    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    public void delete(Long id) {
        Model model = getById(id);
        modelRepository.delete(model);
    }

    private Model getById(Long id) {
        return modelRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProductServiceException("Model not found: " + id, Response.Status.NOT_FOUND));
    }

}
