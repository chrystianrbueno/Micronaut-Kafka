    package com.example.messaging;
import com.example.domain.Order;
import com.example.domain.Shipment;
import com.example.domain.ShipmentStatus;
import com.example.service.OrderService;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.reactivex.rxjava3.core.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class ShipmentConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ShipmentConsumer.class);
    private final OrderService orderService;

    public ShipmentConsumer(OrderService orderService){
        this.orderService = orderService;
    }

    @Topic("shipping-topic")
    public Single<Shipment> receive(@KafkaKey String key, Single<Shipment> shipmentFlowable){
        return shipmentFlowable.doOnSuccess(shipment -> {
            LOG.info("Shipment message received for order{}", shipment.getOrderId());
            LOG.info("Updating order shipment status for order {}", ShipmentStatus.SHIPPED);
            Order order = orderService.getOrderById(shipment.getOrderId());
            order.setShipmentStatus(ShipmentStatus.SHIPPED);
            orderService.updateOrder(order);
            LOG.info("Order shipment status updated");
        });
    }
}