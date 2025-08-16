package se.example.product.mapper;
import se.example.api.core.product.Product;
import se.example.product.persistence.ProductEntity;

public class ProductMapper {

    public static Product mapToProduct(ProductEntity entity) {
        if (entity == null) {
            return null;
        }   
        return new Product(entity.getProductId(), entity.getName(), entity.getWeight(), null);
    }   

    public static ProductEntity mapToEntity(Product product) {
        if (product == null) {
            return null;
        }   
        return new ProductEntity(product.getProductId(), product.getName(), product.getWeight());
    }

}
