package se.example.product;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"se.example.product",
		"se.example.api.core.product",
		"se.example.util.http"
})
public class ProductApplication {

	private static final Logger logger = LoggerFactory.getLogger(ProductApplication.class);

	public static void main(String[] args) {
	var ctx =	SpringApplication.run(ProductApplication.class, args);

	String mongoDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
	String mongoDbHost =ctx.getEnvironment().getProperty("spring.data.mongodb.host");
	logger.info("Connected to MongoDB at {}:{}", mongoDbHost, mongoDbPort);


	}


}
