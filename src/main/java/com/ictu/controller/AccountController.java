package com.ictu.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ictu.entity.Customer;
import com.ictu.repository.CustomersRepository;
import com.ictu.repository.OrderRepository;
import com.ictu.service.ShoppingCartService;
import com.ictu.service.WishListService;

@Controller
public class AccountController extends CommonController {

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	CustomersRepository customersRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	WishListService wishListService;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping(value = "/account")
	public String account(Model model, Principal principal) {

		model.addAttribute("customer", new Customer());
		Customer customer = customersRepository.FindByEmail(principal.getName()).get();
		model.addAttribute("customer", customer);

		List<Object[]> listO2 = orderRepository.orderByCustomerId(customer.getCustomerId());
		model.addAttribute("orders2", listO2);

		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "site/account";
	}

	@GetMapping("/loadUpdatePassword")
	public String load() {
		return "site/changePassword";
	}

	@RequestMapping(value = "/updatePassword")
	public String updatePassword(ModelMap model, Principal principal, @RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword) {

		Customer customer = customersRepository.FindByEmail(principal.getName()).get();

		if (bCryptPasswordEncoder.matches(oldPassword, customer.getPassword())) {
			customer.setPassword(bCryptPasswordEncoder.encode(newPassword));
			customersRepository.save(customer);

			model.addAttribute("message", "Đổi mật khẩu thành công!");

			return "site/changePassword";
		} else {
			model.addAttribute("error", "Mật khẩu cũ không đúng!");
		}

		return "site/changePassword";
	}
}
