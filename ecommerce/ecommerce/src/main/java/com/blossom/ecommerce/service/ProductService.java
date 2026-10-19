package com.blossom.ecommerce.service;

import com.blossom.ecommerce.dto.request.ProductRequest;
import com.blossom.ecommerce.dto.response.ProductResponse;
import com.blossom.ecommerce.exception.ResourceNotFoundException;
import com.blossom.ecommerce.model.DigitalProduct;
import com.blossom.ecommerce.model.PhysicalProduct;
import com.blossom.ecommerce.model.Product;
import com.blossom.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // --- Búsqueda (Ya lo teníamos) ---
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.searchProducts(name, category, minPrice, maxPrice)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- Obtener por ID ---
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapToResponse(product);
    }

    // --- Crear Producto ---
    // Limpiamos la caché porque hay nuevos datos
    @Transactional
    @CacheEvict(value = "productsSearch", allEntries = true) 
    public ProductResponse createProduct(ProductRequest request) {
        Product product = createProductEntityFromRequest(request);
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    // --- Actualizar Producto ---
    @Transactional
    @CacheEvict(value = "productsSearch", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Actualizamos datos comunes
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());

        // Actualizamos datos específicos de la herencia
        if (product instanceof PhysicalProduct physical) {
            physical.setWeightKg(request.getWeightKg());
            physical.setShippingDimensions(request.getShippingDimensions());
        } else if (product instanceof DigitalProduct digital) {
            digital.setDownloadLink(request.getDownloadLink());
            digital.setFileSizeMb(request.getFileSizeMb());
        }

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    // --- Eliminar Producto ---
    @Transactional
    @CacheEvict(value = "productsSearch", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // --- Métodos Utilitarios ---
    private Product createProductEntityFromRequest(ProductRequest request) {
        if ("PHYSICAL".equalsIgnoreCase(request.getProductType())) {
            PhysicalProduct p = new PhysicalProduct();
            p.setName(request.getName());
            p.setCategory(request.getCategory());
            p.setPrice(request.getPrice());
            p.setWeightKg(request.getWeightKg());
            p.setShippingDimensions(request.getShippingDimensions());
            return p;
        } else if ("DIGITAL".equalsIgnoreCase(request.getProductType())) {
            DigitalProduct p = new DigitalProduct();
            p.setName(request.getName());
            p.setCategory(request.getCategory());
            p.setPrice(request.getPrice());
            p.setDownloadLink(request.getDownloadLink());
            p.setFileSizeMb(request.getFileSizeMb());
            return p;
        }
        throw new IllegalArgumentException("Tipo de producto inválido. Use PHYSICAL o DIGITAL");
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        
        if (product instanceof PhysicalProduct) {
            response.setProductType("PHYSICAL");
        } else if (product instanceof DigitalProduct) {
            response.setProductType("DIGITAL");
        }
        return response;
    }
}