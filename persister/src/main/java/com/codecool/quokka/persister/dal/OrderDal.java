package com.codecool.quokka.persister.dal;

import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.Orders;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

public interface OrderDal extends CrudRepository<Orders, UUID> {

    @Modifying
    @Transactional
    @Query("update Orders o set o.price= :price, o.status= :status, o.sellPositionId= :sellPositionId where o.id= :id")
    void updatePriceById(@Param("price") BigDecimal price , @Param("id") UUID id, @Param("status") OrderStatus status, @Param("sellPositionId") UUID sellPositionId);
}
