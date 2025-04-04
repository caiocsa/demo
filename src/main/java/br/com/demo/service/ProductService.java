package br.com.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.demo.dto.ProductDTO;
import br.com.demo.exception.ProductInOrderException;
import br.com.demo.exception.ResourceNotFoundException;
import br.com.demo.mapper.ProductMapper;
import br.com.demo.model.Order;
import br.com.demo.model.Product;
import br.com.demo.repository.OrderRepository;
import br.com.demo.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public List<ProductDTO> getAllProducts() {
        logger.info("Consultando todos os Produtos");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        logger.info("Consultando Produto com o ID {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto com ID %s não encontrado. " .formatted(id)));
        return productMapper.toDTO(product);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        logger.info("Criando novo Produto com o nome {}", productDTO.getName());
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        logger.info("Atualizando Produto com ID {}", id);
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto com ID %s não encontrado. " .formatted(id)));
        if (isProductInOrder(id)) {
            throw new ProductInOrderException("Não é possível atualizar Produto que pertence a uma Ordem.");
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    public void deleteProduct(Long id) {
        logger.info("Deletando Produto com ID {}", id);
        if (isProductInOrder(id)) {
            throw new ProductInOrderException("Não é possível deletar produto que pertence a uma Ordem.");
        }
        productRepository.deleteById(id);
    }

    private boolean isProductInOrder(Long productId) {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .anyMatch(order -> order.getProducts().stream()
                        .anyMatch(product -> product.getId().equals(productId)));
    }
}