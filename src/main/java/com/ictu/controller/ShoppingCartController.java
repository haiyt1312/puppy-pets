package com.ictu.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ictu.dto.CartItem;
import com.ictu.entity.Customer;
import com.ictu.entity.Order;
import com.ictu.entity.OrderDetail;
import com.ictu.entity.Product;
import com.ictu.repository.CustomersRepository;
import com.ictu.repository.OrderDetailRepository;
import com.ictu.repository.OrderRepository;
import com.ictu.repository.ProductRepository;
import com.ictu.service.SendMailService;
import com.ictu.service.ShoppingCartService;
import com.ictu.service.WishListService;

@Controller
public class ShoppingCartController extends CommonController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	CustomersRepository customersRepository;

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	WishListService wishListService;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	HttpSession session;

	public ShoppingCartController(ProductRepository productRepository, OrderRepository orderRepository,
			OrderDetailRepository orderDetailRepository, ShoppingCartService shoppingCartService,
			CustomersRepository customersRepository, SendMailService sendMailService, WishListService wishListService) {
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.orderDetailRepository = orderDetailRepository;
		this.shoppingCartService = shoppingCartService;
		this.customersRepository = customersRepository;
		this.sendMailService = sendMailService;
		this.wishListService = wishListService;
	}

	@GetMapping(value = "/carts")
	public String shoppingCart(Model model) {
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);
		double totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}

		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "site/shoppingCart";
	}

	// add cartItem
	@GetMapping(value = "/addToCart")
	public String add(@RequestParam("productId") Integer productId, HttpServletRequest request, Model model) {

		Product product = productRepository.findById(productId).orElse(null);

		session = request.getSession();
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		if (product != null) {
			CartItem item = new CartItem();
			BeanUtils.copyProperties(product, item);
			item.setQuantity(1);
			item.setProduct(product);
			item.setProductId(productId);
			shoppingCartService.add(item);
		}
		session.setAttribute("cartItems", cartItems);
		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "redirect:/carts";
	}

	// delete cartItem
	@SuppressWarnings("unlikely-arg-type")
	@GetMapping(value = "/remove/{id}")
	public String remove(@PathVariable("id") Integer id, HttpServletRequest request, Model model) {
		Product product = productRepository.findById(id).orElse(null);

		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		session = request.getSession();
		if (product != null) {
			CartItem item = new CartItem();
			BeanUtils.copyProperties(product, item);
			item.setProduct(product);
			cartItems.remove(session);
			shoppingCartService.remove(item);
		}
		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "redirect:/carts";
	}

	// delete 1 item
	@GetMapping(value = "/deleteToCart")
	public String delete(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity,
			HttpServletRequest request, Model model) {
		Product product = productRepository.findById(productId).orElse(null);

		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		session = request.getSession();
		if (product != null && quantity > 1) {
			CartItem item = new CartItem();
			BeanUtils.copyProperties(product, item);
			item.setQuantity(-1);
			item.setProduct(product);
			item.setProductId(productId);
			shoppingCartService.add(item);
			session.setAttribute("cartItems", cartItems);
		}

		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "redirect:/carts";
	}

	// show check out
	@GetMapping(value = "/checkout")
	public String checkOut(Model model) {

		Order order = new Order();
		model.addAttribute("order", order);

		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("NoOfItems", shoppingCartService.getCount());
		double totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}

		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "site/checkOut";
	}

	// submit checkout
	@PostMapping(value = "/checkout")
	@Transactional
	public String checkedOut(Model model, Order order, HttpServletRequest request, Principal principal) {

		session = request.getSession();
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();

		Customer c = customersRepository.FindByEmail(principal.getName()).get();

		double totalPrice = 0;

		for (CartItem cartItem : cartItems) {

			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setOrder(order);
			orderDetail.setProduct(cartItem.getProduct());

			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);

			double unitPrice = cartItem.getProduct().getPrice();

			orderDetail.setTotalPrice(price - (price * cartItem.getProduct().getDiscount() / 100));
			orderDetail.setDiscount(cartItem.getProduct().getDiscount());
			orderDetail.setPrice(unitPrice);
			orderDetail.setStatus("Đang Chờ Xử Lý");
			orderDetailRepository.save(orderDetail);

		}

		order.setTotalPrice(totalPrice);
		Date date = new Date();
		order.setOrderDate(date);
		order.setCustomer(c);

		orderRepository.save(order);
		order.getOrderId();

		sendMailService.sendMail(c.getEmail(), "Puppy Pets Shop",
				"<h3>Hi: " + order.getReceiver() + " !</h3> Bạn có một đơn đặt hàng từ Puppy Pets Shop!\r\n <br>"
						+ "<br>" + "Ngày đặt hàng : " + "<h4 style=\"color: black;\">" + order.getOrderDate() + "</h4>"
						+ "<br>" + "Tổng số tiền là: " + "<h4 style=\"color: red;\">" + order.getTotalPrice() + "₫</h4>"
						+ "<br>" + "Cảm ơn bạn đã mua sắm trong cửa hàng của chúng tôi!\r\n ");

		shoppingCartService.clear();
		wishListService.clear();
		session.removeAttribute("cartItems");
		model.addAttribute("orderId", order.getOrderId());
		model.addAttribute("totalCartItemWishs", wishListService.getCount());
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		return "site/checkout_success";
	}
}
