package com.example.service;

import com.example.domain.Order;
import com.example.domain.Shipment;
import com.example.messaging.ShipmentProducer;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class ShippingService {
    private static final Logger LOG = LoggerFactory.getLogger(ShippingService.class);

    private final ShipmentProducer shipmentProducer;
    private final List<Shipment> shipments = Collections.synchronizedList(new ArrayList<>());

    public ShippingService(ShipmentProducer shipmentProducer) {
        this.shipmentProducer = shipmentProducer;
    }

    public Shipment getShipmentById(Long id) {
        Shipment shipment;
        synchronized (shipments) {
            shipment = shipments.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
        }
        return shipment;
    }

    public List<Shipment> listShipments() {
        return shipments;
    }

    public void updateShipment(Shipment shipment) {
        Shipment existingShipment = getShipmentById(shipment.getId());
        synchronized (shipments) {
            int i = shipments.indexOf(existingShipment);
            shipments.set(i, shipment);
        }
    }

    public Shipment newShipment(Order order) {
        Shipment shipment = new Shipment((long) shipments.size(), order.getId(), new Date());
        synchronized (shipments) {
            shipments.add(shipment);
        }
        LOG.info("Shipment created!");
        LOG.info("Sending shipment message...");
        shipmentProducer.sendMessage(UUID.randomUUID().toString(), shipment);
        LOG.info("Shipment message sent!");
        return shipment;
    }
}
