package se.example.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import se.example.api.core.product.Product;
import se.example.api.core.product.ProductService;
import se.example.api.exception.InvalidInputException;
import se.example.api.exception.NotFoundException;
import se.example.product.persistence.ProductRepository;
import se.example.product.mapper.ProductMapper;
import se.example.util.http.ServiceUtil;
import se.example.product.persistence.ProductEntity;

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
    @GetMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        ProductEntity productEntity = productRepository.findByProductId(productId).orElseThrow(()
                -> new NotFoundException("No product found for productId: " + productId));

        LOG.debug("Found a product entity: {}", productEntity);
        Product product = ProductMapper.mapToProduct(productEntity);
        LOG.debug("Mapped product entity to product: {}", product);
        return product;
    }

    @Override
    @PostMapping("/product")
    public Product createProduct(@RequestBody Product body) {
        LOG.debug("/product create called for productId={}", body.getProductId());
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        ProductEntity productEntity = ProductMapper.mapToEntity(body);
        if (productEntity == null) {
            throw new InvalidInputException("Invalid product: " + body);
        }
        productEntity = productRepository.save(productEntity);
        LOG.debug("Created a product entity: {}", productEntity);
        return new Product(productEntity.getProductId(), productEntity.getName(), productEntity.getWeight(), serviceUtil.getServiceAddress());

    }

    @Override
    @DeleteMapping("/product/{productId}")
    public void deleteProduct(@PathVariable int productId) {
        LOG.debug("/product/{}/delete called for productId={}", productId, productId);
        productRepository.findByProductId(productId).ifPresent(productEntity -> {
            LOG.debug("Deleting product with productId: {}", productId);
            productRepository.delete(productEntity);
        });
    }

}
