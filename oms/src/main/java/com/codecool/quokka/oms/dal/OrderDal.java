package com.codecool.quokka.oms.dal;

import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderDal extends JpaRepository<Orders, UUID> {

    Slice<Orders> findAllByStatus(OrderStatus status, Pageable page);
    Optional<Orders> findOrdersById(UUID id);

    Slice<Orders> findAllByIdIn(Set<UUID> list, Pageable page);
}
