package com.blossom.ecommerce.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("DIGITAL")
@Data
@EqualsAndHashCode(callSuper = true)
public class DigitalProduct extends Product {
    private String downloadLink;
    private Double fileSizeMb;
}
