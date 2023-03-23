package com.ictu.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.ictu.dto.CartItemWish;
import com.ictu.entity.Product;

@Service
public interface WishListService {

	int getCount();

	void clear();

	Collection<CartItemWish> getCartItemWishs();

	void remove(CartItemWish item);

	void add(CartItemWish item);

	void remove(Product product);

}
