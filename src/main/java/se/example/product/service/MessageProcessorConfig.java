package se.example.product.service;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.example.api.core.product.Product;
import se.example.api.core.product.ProductService;
import se.example.api.event.Event;
import se.example.api.exception.EventProcessingException;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final ProductService productService;

    @Autowired
    public MessageProcessorConfig(ProductService productService) {
        this.productService = productService;
    }

    @Bean
    Consumer<Event<Integer, Product>> messageProcessor() {
        return event -> {
            LOGGER.debug("Process event: {}", event);
            switch (event.getEventType()) {
                case CREATE:
                    Product product = event.getData();
                    LOGGER.debug("Create product with ID: {}", product.getProductId());
                    productService.createProduct(product).block();
                    break;
                case DELETE:
                    Integer productId = event.getKey();
                    LOGGER.debug("Delete product with ID: {}", productId);
                    productService.deleteProduct(productId).block();
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType()
                            + ", expected a CREATE or DELETE event";
                    LOGGER.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }
            LOGGER.debug("Event processed successfully: {}", event);
        };
    }

}
