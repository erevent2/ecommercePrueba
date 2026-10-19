package com.blossom.ecommerce.controller;

import com.blossom.ecommerce.dto.request.ProductRequest;
import com.blossom.ecommerce.dto.response.ProductResponse;
import com.blossom.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock ProductService productService;
    @InjectMocks ProductController productController;

    @Test
    void searchReturnsProductsAndPassesAllFilters() {
        List<ProductResponse> products = List.of(new ProductResponse());
        BigDecimal min = BigDecimal.TEN;
        BigDecimal max = BigDecimal.valueOf(100);
        when(productService.searchProducts("book", "Books", min, max)).thenReturn(products);

        var result = productController.searchProducts("book", "Books", min, max);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(products, result.getBody());
    }

    @Test
    void getsProductById() {
        ProductResponse response = new ProductResponse();
        when(productService.getProductById(4L)).thenReturn(response);

        var result = productController.getProductById(4L);

        assertSame(response, result.getBody());
        verify(productService).getProductById(4L);
    }

    @Test
    void createsProductWithCreatedStatus() {
        ProductRequest request = new ProductRequest();
        ProductResponse response = new ProductResponse();
        when(productService.createProduct(request)).thenReturn(response);

        var result = productController.createProduct(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void updatesProduct() {
        ProductRequest request = new ProductRequest();
        ProductResponse response = new ProductResponse();
        when(productService.updateProduct(5L, request)).thenReturn(response);

        var result = productController.updateProduct(5L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void deletesProductWithNoContentStatus() {
        var result = productController.deleteProduct(6L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(productService).deleteProduct(6L);
    }
}
