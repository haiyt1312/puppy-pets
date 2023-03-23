package com.ictu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ictu.entity.Product;
import com.ictu.repository.ProductRepository;
import com.ictu.service.ShoppingCartService;
import com.ictu.service.WishListService;

@Controller
public class IndexController extends CommonController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	WishListService wishListService;

	@GetMapping(value = "/")
	public String index(Model model) {
		listProduct10(model);
		topProduct10(model);
		topDiscount10(model);
		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return "site/index";
	}

	// Hiển thị 10 sản phẩm mới nhất
	@ModelAttribute("listProduct10")
	public List<Product> listProduct10(Model model) {
		List<Product> productList = productRepository.listProduct10();
		model.addAttribute("productList", productList);
		return productList;
	}

	// Hiển thị Top 10 sản phẩm bán chạy nhất.
	public List<Product> topProduct10(Model model) {
		List<Product> productList = productRepository.topSellingProduct10();
		model.addAttribute("listTop10Product", productList);
		return productList;
	}

	// Hiển thị Top 10 sản phẩm khuyến mãi cao nhất.
	public List<Product> topDiscount10(Model model) {
		List<Product> productList = productRepository.topDiscount10();
		model.addAttribute("listTop10Discount", productList);
		return productList;
	}

}
