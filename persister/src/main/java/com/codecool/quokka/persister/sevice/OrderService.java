package com.codecool.quokka.persister.sevice;

import com.codecool.quokka.model.mqconfig.Config;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.position.Position;
import com.codecool.quokka.persister.dal.OrderDal;
import com.codecool.quokka.persister.dal.PositionDal;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private RabbitTemplate template;
    private OrderDal orderDal;
    private PositionDal positionDal;

    @Autowired
    public OrderService(RabbitTemplate template, OrderDal orderDal, PositionDal positionDal) {
        this.template = template;
        this.orderDal = orderDal;
        this.positionDal = positionDal;
    }

    @RabbitListener(queues = Config.ORDER_QUEUE)
    public void addNewOrder(Orders order) {
        if (orderDal.findById(order.getId()).isPresent()) {
            orderDal.updatePriceById(order.getPrice(), order.getId(), order.getStatus());
            return;
        }
        orderDal.save(order);
    }

    @RabbitListener(queues = Config.POSITION_QUEUE)
    public void addNewPosition(Position position) {
        positionDal.save(position);
    }
}
