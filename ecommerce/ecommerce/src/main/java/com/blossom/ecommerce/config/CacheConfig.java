package com.blossom.ecommerce.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // La anotación @EnableCaching activa el manejo de caché mediante anotaciones en nuestros repositorios o servicios.
}
