package com.ictu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ictu.service.ShoppingCartService;
import com.ictu.service.WishListService;

@Controller
public class ContactController extends CommonController {

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	WishListService wishListService;

	@GetMapping(value = "/contact")
	public String contact(Model model) {

		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return "site/contact";
	}
}
