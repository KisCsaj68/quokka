package com.codecool.quokka.oms.dal;

import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderDal extends JpaRepository<Orders, UUID> {

    List<Orders> findAllByStatus(OrderStatus status);
    Optional<Orders> findOrdersById(UUID id);

    Set<Orders> findAllByIdIn(Set<UUID> list);
}
