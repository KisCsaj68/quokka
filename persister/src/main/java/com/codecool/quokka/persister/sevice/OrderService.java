package com.codecool.quokka.persister.sevice;

import com.codecool.quokka.model.mqconfig.Config;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.position.Position;
import com.codecool.quokka.persister.dal.OrderDal;
import com.codecool.quokka.persister.dal.PositionDal;
import com.codecool.quokka.persister.metrics.Metrics;
import io.prometheus.client.Histogram;
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
        Metrics.PERSIST_REQUEST.labels("order").inc();
        try (Histogram.Timer ignored = Metrics.PERSIST_TIME_DURATION.labels("order").startTimer()) {
            if (orderDal.findById(order.getId()).isPresent() && order.getStatus().equals(OrderStatus.FILLED)) {
                orderDal.updatePriceById(order.getPrice(), order.getId(), order.getStatus(), order.getSellPositionId());
                return;
            }
            else if(orderDal.findById(order.getId()).isPresent() && order.getStatus().equals(OrderStatus.OPEN)){
                return;
            }
            orderDal.save(order);
        }
    }

    @RabbitListener(queues = Config.POSITION_QUEUE)
    public void addNewPosition(Position position) {
        Metrics.PERSIST_REQUEST.labels("position").inc();
        try (Histogram.Timer ignored = Metrics.PERSIST_TIME_DURATION.labels("position").startTimer()) {
            positionDal.save(position);
        }
    }
}
