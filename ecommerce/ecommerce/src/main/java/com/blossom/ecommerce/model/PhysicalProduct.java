package com.blossom.ecommerce.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("PHYSICAL")
@Data
@EqualsAndHashCode(callSuper = true)
public class PhysicalProduct extends Product {
    private Double weightKg;
    private String shippingDimensions;
}
