package com.web.demo.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dtos.InventoryCreateEvent;
import com.product.dtos.InventoryVariantStock;
import com.product.topics.KafkaTopicConstants;
import com.web.demo.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishProductCreated(Product product) throws JsonProcessingException {
        InventoryCreateEvent inventoryCreateEvent = toInventoryEvent(product);
        String inventoryCreateJson = objectMapper.writeValueAsString(inventoryCreateEvent);

        kafkaTemplate.send(KafkaTopicConstants.INVENTORY_CREATE_EVENT, UUID.randomUUID().toString(),inventoryCreateJson);
    }

    public InventoryCreateEvent toInventoryEvent(Product request) {
        List<InventoryVariantStock> variants = request.getVariants().stream()
                .map(v -> new InventoryVariantStock(
                        v.getSize(),
                        v.getColor(),
                        v.getConfiguration(),
                        10
                ))
                .toList();

        return new InventoryCreateEvent(
                request.getProductId(),
                request.getSku(),
                variants
        );
    }
}
