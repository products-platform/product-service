package com.web.demo.producer;

import com.product.dtos.ProductCreateDto;
import com.product.topics.KafkaTopicConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishProductCreated(Long productId, String name) {
        ProductCreateDto productCreateDto = new ProductCreateDto(productId,name);
        String productCreateJson = objectMapper.writeValueAsString(productCreateDto);

        kafkaTemplate.send(KafkaTopicConstants.PRODUCT_CREATED_TOPIC, UUID.randomUUID().toString(),productCreateJson);
    }
}
