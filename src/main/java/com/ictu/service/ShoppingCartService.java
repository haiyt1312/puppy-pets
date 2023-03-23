package com.ictu.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.ictu.dto.CartItem;
import com.ictu.entity.Product;

@Service
public interface ShoppingCartService {

	int getCount();

	void clear();

	Collection<CartItem> getCartItems();

	void remove(CartItem item);

	void add(CartItem item);

	void remove(Product product);

}
