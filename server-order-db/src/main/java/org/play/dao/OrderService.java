package org.play.dao;


import org.play.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderService extends JpaRepository<Order, Long> {
}
