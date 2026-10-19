package com.blossom.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotBlank(message = "El tipo de producto (PHYSICAL o DIGITAL) es obligatorio")
    private String productType;

    // Campos para PhysicalProduct
    private Double weightKg;
    private String shippingDimensions;

    // Campos para DigitalProduct
    private String downloadLink;
    private Double fileSizeMb;
}
