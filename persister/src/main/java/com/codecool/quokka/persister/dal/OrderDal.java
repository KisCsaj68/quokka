package com.codecool.quokka.persister.dal;

import com.codecool.quokka.model.order.AssetOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderDal extends CrudRepository<AssetOrder, Long> {
}
