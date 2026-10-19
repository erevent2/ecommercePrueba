package com.blossom.ecommerce.repository;

import com.blossom.ecommerce.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        if (name != null) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(product.get("name")),
                    "%" + name.toLowerCase(Locale.ROOT) + "%"));
        }
        if (category != null) {
            predicates.add(criteriaBuilder.equal(product.get("category"), category));
        }
        if (minPrice != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(product.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(product.get("price"), maxPrice));
        }

        return entityManager.createQuery(query.select(product).where(predicates.toArray(Predicate[]::new)))
                .getResultList();
    }
}
