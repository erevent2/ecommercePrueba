package com.blossom.ecommerce.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private String productType; // PHYSICAL o DIGITAL
}