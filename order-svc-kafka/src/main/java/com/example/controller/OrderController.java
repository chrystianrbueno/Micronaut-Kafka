package com.example.controller;

import com.example.domain.Order;
import com.example.service.OrderService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.util.List;

@Controller("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public HttpResponse<List<Order>> listOrders(){
        return HttpResponse.ok(orderService.listOrders());
    }

    @Get(value = "/{id}",produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Order> listOrders(Long id){
        Order order = orderService.getOrderById(id);
        if(order != null){
            return HttpResponse.ok(order);
        }
        return HttpResponse.notFound();
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Order> newOrder(@Body Order order){
        return HttpResponse.created(orderService.newOrder(order));
    }

    @Put(produces = MediaType.APPLICATION_JSON)
    public HttpResponse updateOrder(@Body Order order){
        orderService.updateOrder(order);
        return HttpResponse.ok();
    }

}
