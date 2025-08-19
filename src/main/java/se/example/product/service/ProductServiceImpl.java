package se.example.product.service;

import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import se.example.api.core.product.Product;
import se.example.api.core.product.ProductService;
import se.example.api.exception.InvalidInputException;
import se.example.api.exception.NotFoundException;
import se.example.product.mapper.ProductMapper;
import se.example.product.persistence.ProductEntity;
import se.example.product.persistence.ProductRepository;
import se.example.util.http.ServiceUtil;

/**
 * This class implements the ProductService interface and provides methods to
 * handle product-related operations.
 */
@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ServiceUtil serviceUtil;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ServiceUtil serviceUtil) {
        this.productRepository = productRepository;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Product> getProduct(@PathVariable int productId) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
         Mono<Product> product = productRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log(LOG.getName(), Level.FINE)
                .map(e -> ProductMapper.mapToProduct(e)).map(e -> setServiceAddress(e));

        return product;
    }

    @Override
    public Mono<Product> createProduct(@RequestBody Product body) {
        LOG.debug("/product create called for productId={}", body.getProductId());
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        ProductEntity productEntity = ProductMapper.mapToEntity(body);
        if (productEntity == null) {
            throw new InvalidInputException("Invalid product: " + body);
        }
        Mono<ProductEntity> newEntity = productRepository.save(productEntity);
        LOG.debug("Created a product entity: {}", productEntity);
        return newEntity
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .map(e -> ProductMapper.mapToProduct(e))
                .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Void> deleteProduct(@PathVariable int productId) {
        LOG.debug("/product/{}/delete called for productId={}", productId, productId);
       return productRepository.findByProductId(productId)
                .log(LOG.getName(), Level.FINE)
                .map(e -> productRepository.delete(e))
                .flatMap(e -> e);
       
    }


    private Product setServiceAddress(Product product) {
        product.setServiceAddress(serviceUtil.getServiceAddress());
        return product;
    }

}
