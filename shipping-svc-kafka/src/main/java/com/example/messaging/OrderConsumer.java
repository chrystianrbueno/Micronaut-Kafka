    package com.example.messaging;

    import com.example.domain.Order;
    import com.example.domain.Shipment;
    import com.example.service.ShippingService;
    import io.micronaut.configuration.kafka.annotation.KafkaKey;
    import io.micronaut.configuration.kafka.annotation.KafkaListener;
    import io.micronaut.configuration.kafka.annotation.OffsetReset;
    import io.micronaut.configuration.kafka.annotation.Topic;
    import io.reactivex.rxjava3.core.Single;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    @KafkaListener(offsetReset = OffsetReset.EARLIEST)
    public class OrderConsumer {
        private static final Logger LOG = LoggerFactory.getLogger(OrderConsumer.class);
        private final ShippingService shippingService;

        public OrderConsumer(ShippingService shippingService){
            this.shippingService = shippingService;
        }

        @Topic("order-topic")
        public Single<Order> receive(@KafkaKey String key, Single<Order> orderFlowable){
            return orderFlowable.doOnSuccess(order ->{
                LOG.info("Order {} received", order.getId());
                LOG.info("Creating shipment for order {}....", order.getId());
                /* shipping is slow! */
                Thread.sleep(15*1000);
                Shipment shipment = shippingService.newShipment(order);
                LOG.info("Shipped order {} with shipment ID {}...", order.getId(), shipment.getId());
            });
        }
}