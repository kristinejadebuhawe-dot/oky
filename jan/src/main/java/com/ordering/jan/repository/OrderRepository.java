package com.ordering.jan.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ordering.jan.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
}

