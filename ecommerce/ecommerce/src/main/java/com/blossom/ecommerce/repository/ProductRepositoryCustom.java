package com.blossom.ecommerce.repository;

import com.blossom.ecommerce.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);
}
