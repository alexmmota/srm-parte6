package br.com.srm.service;

import br.com.srm.exception.BusinessServiceException;
import br.com.srm.model.ProductEntity;
import br.com.srm.repository.ProductRepository;
import br.com.srm.utils.UserContextHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RefreshScope
public class ProductService {

    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Value("${srm.message.fail}")
    private String messageFail;

    public ProductEntity save(ProductEntity product) {
        log.info("m=save, product={}", product);
        if (productRepository.findByIsbn(product.getIsbn()) != null)
            throw new BusinessServiceException("Já existe um produto com esse codigo ISBN");
        return productRepository.save(product);
    }

    public void delete(String isbn) {
        logger.info("m=delete, isbn={}", isbn);
        productRepository.deleteById(isbn);
    }

    public ProductEntity addAmount(String isbn, Integer amount) {
        log.info("m=addAmount, isbn={}, amount={}", isbn, amount);
        ProductEntity product = findByISBN(isbn);
        product.setAmount(product.getAmount() + amount);
        return productRepository.save(product);
    }

    public ProductEntity subtractAmount(String isbn, Integer amount) {
        log.info("m=subtractAmount, isbn={}, amount={}", isbn, amount);
        ProductEntity product = findByISBN(isbn);
        if (product.getAmount() < amount)
            throw new BusinessServiceException(messageFail);
        product.setAmount(product.getAmount() - amount);
        return productRepository.save(product);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackProductList")
    public ProductEntity findByISBN(String isbn) {
        log.info("m=findByISBN, idbn={}, correlationId={}", isbn, UserContextHolder.getContext().getCorrelationId());
        Optional<ProductEntity> product = productRepository.findById(isbn);
        if (product.isPresent())
            return product.get();
        return null;
    }

    private ProductEntity buildFallbackProductList(String isbn) {
        ProductEntity product = new ProductEntity();
        product.setAmount(0);
        product.setIsbn("00000-00000");
        product.setName("Ops! Não foi possível buscar seu produto agora!");
        return product;
    }


}
