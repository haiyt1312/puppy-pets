package com.ictu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictu.entity.Order;
import com.ictu.repository.OrderRepository;
import com.ictu.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

	@Autowired
	OrderRepository repo;

	public List<Order> listAll() {

		return (List<Order>) repo.findAll();
	}
}
