package com.ictu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ictu.entity.Order;

@Service
public interface OrderDetailService {

	List<Order> listAll();

}
