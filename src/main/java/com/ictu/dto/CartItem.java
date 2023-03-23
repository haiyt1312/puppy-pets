package com.ictu.dto;

import com.ictu.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
	
	private int productId;
	private String name;
	private double unitPrice;
	private int quantity;
	private double totalPrice;
	private Product product;
}
