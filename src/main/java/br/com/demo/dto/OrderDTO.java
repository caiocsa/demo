package br.com.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderDTO {
    private Long id;
    private List<ProductDTO> products;
    private String status;
    private Double totalValue;
}