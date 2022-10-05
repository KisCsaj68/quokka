package com.codecool.quokka.persister.dal;

import com.codecool.quokka.model.order.Orders;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderDal extends CrudRepository<Orders, UUID> {
}
