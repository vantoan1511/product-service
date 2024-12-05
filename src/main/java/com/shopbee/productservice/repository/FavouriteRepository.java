package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.entity.Favourite;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class FavouriteRepository extends AbstractRepository implements PanacheRepository<Favourite> {

    public List<Favourite> findByCriteria(PageRequest pageRequest, String username) {
        if (Objects.isNull(pageRequest)) {
            return listAll();
        }

        return find("username", username).page(pageRequest.getPage() - 1, pageRequest.getSize()).list();
    }

    public Favourite findByProductSlug(String productSlug, String username) {
        return find("productSlug = ?1 and username = ?2", productSlug, username).firstResult();
    }
}
