package br.com.demo.mapper;

import br.com.demo.dto.OrderDTO;
import br.com.demo.model.Order;
import br.com.demo.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    default OrderDTO toDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setProducts(order.getProducts().stream()
            .map(ProductMapper.INSTANCE::toDTO)
            .collect(Collectors.toList()));
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalValue(calculateTotalValue(order));

        return orderDTO;
    }

    default Double calculateTotalValue(Order order) {
        return order.getProducts().stream()
            .map(Product::getPrice)
            .reduce(0.0, Double::sum);
    }

    Order toEntity(OrderDTO orderDTO);
}