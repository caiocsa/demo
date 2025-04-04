package br.com.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.demo.dto.ProductDTO;
import br.com.demo.model.Product;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO productDTO);
}