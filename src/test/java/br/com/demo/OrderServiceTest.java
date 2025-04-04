package br.com.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.demo.dto.OrderDTO;
import br.com.demo.exception.ResourceNotFoundException;
import br.com.demo.mapper.OrderMapper;
import br.com.demo.mapper.ProductMapper;
import br.com.demo.model.Order;
import br.com.demo.repository.OrderRepository;
import br.com.demo.service.OrderService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderMapper orderMapper = OrderMapper.INSTANCE;
    private ProductMapper productMapper = ProductMapper.INSTANCE;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setUp() {
        order = new Order();
        order.setId(1L);
        order.setProducts(new ArrayList<>());
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setProducts(new ArrayList<>());
    }

    @Test
    public void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        List<OrderDTO> orderDTOs = orderService.getAllOrders();
        assertEquals(1, orderDTOs.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderDTO foundOrderDTO = orderService.getOrderById(1L);
        assertEquals(orderDTO.getId(), foundOrderDTO.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetOrderByIdNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderDTO createdOrderDTO = orderService.createOrder(orderDTO);
        assertEquals(orderDTO.getId(), createdOrderDTO.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderDTO updatedOrderDTO = orderService.updateOrder(1L, orderDTO);
        assertEquals(orderDTO.getId(), updatedOrderDTO.getId());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, orderDTO));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);
        orderService.deleteOrder(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }
}