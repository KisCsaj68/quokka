package com.codecool.quokka.persister.dal;

import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.Orders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderDal extends CrudRepository<Orders, UUID> {

    @Query(value = "update Orders o set o.price= :price, o.status= :status where o.id= :id")
    void updatePriceById(@Param("price")BigDecimal price, @Param("id") UUID id, @Param("status") OrderStatus status);
}
