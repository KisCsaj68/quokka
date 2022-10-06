package com.codecool.quokka.persister.sevice;

import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.persister.MQConfig;
import com.codecool.quokka.persister.dal.OrderDal;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class OrderService {
    private RabbitTemplate template;
    private OrderDal orderDal;

    @Autowired
    public OrderService(RabbitTemplate template, OrderDal orderDal) {
        this.template = template;
        this.orderDal = orderDal;
    }

    @RabbitListener(queues = MQConfig.QUEUE)
    public void addNewOrder(Orders order) {
        if (orderDal.findById(order.getId()).isPresent()) {
            orderDal.updatePriceById(order.getPrice(), order.getId(), order.getStatus());
            return;
        }
        System.out.println("Order from Rabbit listener" + order);
        orderDal.save(order);
    }


}
