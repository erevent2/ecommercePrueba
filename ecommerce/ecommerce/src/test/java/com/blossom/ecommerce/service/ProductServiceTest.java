package com.blossom.ecommerce.service;

import com.blossom.ecommerce.dto.request.ProductRequest;
import com.blossom.ecommerce.dto.response.ProductResponse;
import com.blossom.ecommerce.exception.ResourceNotFoundException;
import com.blossom.ecommerce.model.DigitalProduct;
import com.blossom.ecommerce.model.PhysicalProduct;
import com.blossom.ecommerce.model.Product;
import com.blossom.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private PhysicalProduct mockProduct;

    @BeforeEach
    void setUp() {
        mockProduct = new PhysicalProduct();
        mockProduct.setId(1L);
        mockProduct.setName("Teclado Mecánico");
        mockProduct.setCategory("Electrónica");
        mockProduct.setPrice(new BigDecimal("100.00"));
        mockProduct.setWeightKg(1.2);
    }

    @Test
    @DisplayName("Debe retornar lista de productos mapeados correctamente al buscar")
    void searchProducts_should_returnProductList_when_criteriaMatches() {
        // Arrange (Preparar)
        when(productRepository.searchProducts(any(), any(), any(), any()))
                .thenReturn(List.of(mockProduct));

        // Act (Actuar)
        List<ProductResponse> result = productService.searchProducts(null, null, null, null);

        // Assert (Afirmar)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Teclado Mecánico", result.get(0).getName());
        assertEquals("PHYSICAL", result.get(0).getProductType());
        
        verify(productRepository, times(1)).searchProducts(null, null, null, null);
    }

    @Test
    @DisplayName("Debe guardar un producto y retornar el DTO")
    void createProduct_should_saveAndReturnResponse() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Mouse");
        request.setCategory("Electrónica");
        request.setPrice(new BigDecimal("50.00"));
        request.setProductType("PHYSICAL");

        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals("Teclado Mecánico", response.getName()); // El mock retorna teclado
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void searchProducts_mapsDigitalProducts() {
        DigitalProduct digital = new DigitalProduct();
        digital.setName("E-book");
        when(productRepository.searchProducts("book", null, null, null)).thenReturn(List.of(digital));

        ProductResponse result = productService.searchProducts("book", null, null, null).get(0);

        assertEquals("DIGITAL", result.getProductType());
        assertEquals("E-book", result.getName());
    }

    @Test
    void getProductById_returnsMappedProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        ProductResponse result = productService.getProductById(1L);

        assertEquals(1L, result.getId());
        assertEquals("PHYSICAL", result.getProductType());
    }

    @Test
    void getProductById_throwsWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_createsDigitalProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("E-book");
        request.setCategory("Libros");
        request.setPrice(BigDecimal.TEN);
        request.setProductType("digital");
        request.setDownloadLink("https://example.test/book");
        request.setFileSizeMb(12.5);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse result = productService.createProduct(request);

        assertEquals("DIGITAL", result.getProductType());
        verify(productRepository).save(argThat(product -> product instanceof DigitalProduct
                && ((DigitalProduct) product).getFileSizeMb().equals(12.5)));
    }

    @Test
    void createProduct_rejectsUnknownType() {
        ProductRequest request = new ProductRequest();
        request.setProductType("UNKNOWN");

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_updatesPhysicalSpecificAndCommonFields() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated");
        request.setCategory("Office");
        request.setPrice(new BigDecimal("25.00"));
        request.setWeightKg(2.5);
        request.setShippingDimensions("10x20x30");
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);

        ProductResponse result = productService.updateProduct(1L, request);

        assertEquals("Updated", result.getName());
        assertEquals(2.5, mockProduct.getWeightKg());
        assertEquals("10x20x30", mockProduct.getShippingDimensions());
    }

    @Test
    void updateProduct_updatesDigitalSpecificFields() {
        DigitalProduct digital = new DigitalProduct();
        when(productRepository.findById(2L)).thenReturn(Optional.of(digital));
        when(productRepository.save(digital)).thenReturn(digital);
        ProductRequest request = new ProductRequest();
        request.setName("Manual");
        request.setCategory("Docs");
        request.setPrice(BigDecimal.ONE);
        request.setDownloadLink("https://example.test/manual");
        request.setFileSizeMb(3.0);

        ProductResponse result = productService.updateProduct(2L, request);

        assertEquals("DIGITAL", result.getProductType());
        assertEquals("https://example.test/manual", digital.getDownloadLink());
        assertEquals(3.0, digital.getFileSizeMb());
    }

    @Test
    void updateProduct_throwsWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, new ProductRequest()));
    }

    @Test
    void deleteProduct_deletesExistingProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_throwsWhenProductDoesNotExist() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, never()).deleteById(any());
    }
}