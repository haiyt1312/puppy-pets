package com.ictu.controller.admin;

import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ictu.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ictu.entity.Customer;
import com.ictu.repository.CustomersRepository;

@Controller
public class IndexAdminController {
	
	@Autowired
	CustomersRepository customersRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;
	
	@GetMapping(value = "admin/home")
	public String indexAdmin(Model model, Principal principal) {
		model.addAttribute("customer", new Customer());
		Customer customer = customersRepository.FindByEmail(principal.getName()).get();
		model.addAttribute("customer", customer);

		List<Object[]> dashboard = customersRepository.getDashboard();
		model.addAttribute("dashboard", dashboard);

		List<Object[]> repoMonth = orderDetailRepository.repoMonth();
		List<Double> data = new ArrayList<Double>();
		for (Object[] objectArray : repoMonth) {
			for (Object object : objectArray) {
				data.add((Double) object);
			}
		}
		model.addAttribute("data", data);

		List<Object[]> repoCategory = orderDetailRepository.repoCategory();
		List<BigInteger> dataCategory = new ArrayList<BigInteger>();
		for (Object[] objectArray : repoCategory) {
			for (Object object : objectArray) {
				dataCategory.add((BigInteger) object);
			}
		}
		model.addAttribute("dataCategory", dataCategory);
		
		return "admin/index";
	}

}
