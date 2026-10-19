package com.blossom.ecommerce.repository;

import com.blossom.ecommerce.model.DigitalProduct;
import com.blossom.ecommerce.model.PhysicalProduct;
import com.blossom.ecommerce.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProductRepositorySearchTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("productsSearch").clear();
        productRepository.deleteAll();
        productRepository.saveAll(List.of(
                product("FilterTest Keyboard", "Electrónica", "100.00"),
                product("FilterTest E-book", "Libros", "20.00"),
                product("Other product", "Otros", "50.00")));
    }

    @Test
    void searchesWithoutOptionalFilters() {
        assertEquals(3, productRepository.searchProducts(null, null, null, null).size());
    }

    @Test
    void appliesNameCategoryAndInclusivePriceFilters() {
        List<Product> result = productRepository.searchProducts(
                "FILTERTEST", "Electrónica", new BigDecimal("100.00"), new BigDecimal("100.00"));

        assertEquals(1, result.size());
        assertEquals("FilterTest Keyboard", result.get(0).getName());
    }

    @Test
    void appliesIndividualFiltersWhenOtherFiltersAreNull() {
        assertEquals(2, productRepository.searchProducts("filtertest", null, null, null).size());
        assertEquals(1, productRepository.searchProducts(null, "Libros", null, null).size());
        assertEquals(1, productRepository.searchProducts(null, null,
                new BigDecimal("90.00"), null).size());
        assertEquals(1, productRepository.searchProducts(null, null, null,
                new BigDecimal("25.00")).size());
    }

    private Product product(String name, String category, String price) {
        Product product;
        if ("Libros".equals(category)) {
            DigitalProduct digitalProduct = new DigitalProduct();
            digitalProduct.setDownloadLink("https://example.test");
            product = digitalProduct;
        } else {
            PhysicalProduct physicalProduct = new PhysicalProduct();
            physicalProduct.setWeightKg(1.0);
            product = physicalProduct;
        }
        product.setName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        return product;
    }
}
