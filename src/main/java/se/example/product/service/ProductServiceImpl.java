package se.example.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import se.example.util.http.ServiceUtil;
import se.example.api.core.product.Product;
import se.example.api.core.product.ProductService;
import se.example.api.exception.InvalidInputException;
import se.example.api.exception.NotFoundException;

@RestController()
public class ProductServiceImpl implements ProductService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

   private final ServiceUtil serviceUtil;

  @Autowired
  public ProductServiceImpl(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  @GetMapping(value = "/product/{productId}", produces = "application/json")
  public Product getProduct(@PathVariable int productId) {
    LOG.debug("/product return the found product for productId={}", productId);

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    if (productId == 13) {
      throw new NotFoundException("No product found for productId: " + productId);
    }

    return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
  }





}
