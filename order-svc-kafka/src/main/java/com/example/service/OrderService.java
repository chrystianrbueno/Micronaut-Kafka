package com.example.service;

import com.example.domain.Order;
import com.example.messaging.OrderProducer;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    public List<Order> orders = new ArrayList<>();

    private final OrderProducer orderProducer;

     public OrderService(OrderProducer orderProducer){
         this.orderProducer = orderProducer;
     }

    public Order getOrderById(Long id) {
        return orders.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Order> listOrders() {
        return orders;
    }

    public void updateOrder(Order order) {
        LOG.info("Existing order update received");
        Order existingOrder = getOrderById(order.getId());
        int i = orders.indexOf(existingOrder);
        orders.set(i, order);
        LOG.info("Order with ID {} has been updated", order.getId());
    }

    public Order newOrder(Order order) {
        LOG.info("New order received");
        order.setId((long) orders.size());
        this.orders.add(order);
        orderProducer.sendMessage(order.getId().toString(), order);
        LOG.info("Order {} has been placed", order.getId());
        return order;
    }
}