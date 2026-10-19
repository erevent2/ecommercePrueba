package com.blossom.ecommerce.repository;

import com.blossom.ecommerce.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // @EntityGraph le dice a Hibernate que cargue la colección 'items' y los 'productos' de cada item en la misma consulta
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}
