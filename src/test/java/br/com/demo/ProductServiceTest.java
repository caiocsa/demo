package br.com.demo;

import br.com.demo.dto.ProductDTO;
import br.com.demo.exception.ProductInOrderException;
import br.com.demo.exception.ResourceNotFoundException;
import br.com.demo.mapper.ProductMapper;
import br.com.demo.model.Order;
import br.com.demo.model.Product;
import br.com.demo.repository.OrderRepository;
import br.com.demo.repository.ProductRepository;
import br.com.demo.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductService productService;

    private ProductMapper productMapper = ProductMapper.INSTANCE;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Produto Teste");
        product.setPrice(100.0);
        
        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Produto Teste");
        productDTO.setPrice(100.0);
    }

    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        List<ProductDTO> productDTOs = productService.getAllProducts();
        assertEquals(1, productDTOs.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductDTO foundProductDTO = productService.getProductById(1L);
        assertEquals(productDTO.getId(), foundProductDTO.getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductDTO createdProductDTO = productService.createProduct(productDTO);
        assertEquals(productDTO.getId(), createdProductDTO.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductDTO updatedProductDTO = productService.updateProduct(1L, productDTO);
        assertEquals(productDTO.getId(), updatedProductDTO.getId());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDTO));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateProductInOrder() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Order order = new Order();
        order.setProducts(Arrays.asList(product));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        assertThrows(ProductInOrderException.class, () -> productService.updateProduct(1L, productDTO));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteProduct() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());
        doNothing().when(productRepository).deleteById(1L);
        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteProductInOrder() {
        Order order = new Order();
        order.setProducts(Arrays.asList(product));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        assertThrows(ProductInOrderException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(0)).deleteById(1L);
    }
}