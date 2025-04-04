package br.com.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.demo.dto.OrderDTO;
import br.com.demo.exception.ResourceNotFoundException;
import br.com.demo.mapper.OrderMapper;
import br.com.demo.mapper.ProductMapper;
import br.com.demo.model.Order;
import br.com.demo.repository.OrderRepository;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderRepository orderRepository;

	private final OrderMapper orderMapper = OrderMapper.INSTANCE;
	private final ProductMapper productMapper = ProductMapper.INSTANCE;

	public List<OrderDTO> getAllOrders() {
		logger.info("Consultando todas as Ordens");
		List<Order> orders = orderRepository.findAll();
		return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
	}

	public OrderDTO getOrderById(Long id) {
		logger.info("Consultando Ordem com ID {}", id);
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ordem com ID %s não encontrada  ".formatted(id)));
		return orderMapper.toDTO(order);
	}

	public OrderDTO createOrder(OrderDTO orderDTO) {
		logger.info("Criando nova Ordem");
		Order order = orderMapper.toEntity(orderDTO);
		order = orderRepository.save(order);
		return orderMapper.toDTO(order);
	}

	public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
		logger.info("Atualizando Ordem com ID {}", id);
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ordem com ID %s não encontrada  ".formatted(id)));
		order.setProducts(orderDTO.getProducts().stream().map(productDTO -> productMapper.toEntity(productDTO))
				.collect(Collectors.toList()));
		order.setStatus(orderDTO.getStatus());
		order = orderRepository.save(order);
		return orderMapper.toDTO(order);
	}

	public void deleteOrder(Long id) {
		logger.info("Deletando Ordem com o ID {}", id);
		orderRepository.deleteById(id);
	}
}