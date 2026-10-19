package com.blossom.ecommerce.repository;

import com.blossom.ecommerce.model.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Cacheable(value = "productsSearch")
    List<Product> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);
}
