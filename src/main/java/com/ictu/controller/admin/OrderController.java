package com.ictu.controller.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.ictu.entity.Category;
import com.ictu.entity.Product;
import com.ictu.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ictu.controller.CommonController;
import com.ictu.dto.OrderExcelExporter;
import com.ictu.entity.Order;
import com.ictu.entity.OrderDetail;
import com.ictu.repository.OrderDetailRepository;
import com.ictu.repository.OrderRepository;
import com.ictu.service.OrderDetailService;

@Controller
public class OrderController extends CommonController {

	@Autowired
	OrderDetailService orderDetailService;

	@Autowired
	OrderRepository orderRepository;
	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@GetMapping(value = "/admin/orders")
	public String orders(Model model) {
		List<OrderDetail> orderDetails = orderDetailRepository.findAll();
		model.addAttribute("orderDetails", orderDetails);

		return "admin/orders";
	}

	// get edit
	@GetMapping("/editOrder/{orderDetailId}")
	public String showEditOrder(@PathVariable("orderDetailId") int orderDetailId, Model model) {
		OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElse(null);
		model.addAttribute("orderDetail", orderDetail);

		return "admin/editOrder";
	}

	// edit order
	@RequestMapping(value = "/editOrder", method = RequestMethod.POST)
	public String editOrder(@ModelAttribute("orderDetail") OrderDetail orderDetail, Model model,
			RedirectAttributes rs) {
		try {
			Product product = productRepository.findById(orderDetail.getProduct().getProductId()).orElse(null);
			Double totalPrice = orderDetail.getQuantity() * (product.getPrice() - (product.getPrice() * (product.getDiscount() * 0.01)));
			Double sumTotalPrice = orderDetailRepository.sumTotalPrice(orderDetail.getOrder().getOrderId());

			Order order = orderRepository.findById(orderDetail.getOrder().getOrderId()).orElse(null);
			order.setReceiver(orderDetail.getOrder().getReceiver());
			order.setAddress(orderDetail.getOrder().getAddress());
			order.setTotalPrice(sumTotalPrice);
			orderRepository.save(order);

			orderDetail.setDiscount(product.getDiscount());
			orderDetail.setTotalPrice(totalPrice);
			OrderDetail orderDetail2 = orderDetailRepository.save(orderDetail);

			model.addAttribute("message", "Cập nhật thành công!");
			model.addAttribute("orderDetail", orderDetailRepository.findById(orderDetail2.getOrderDetailId()));
		}catch (Exception e){
			model.addAttribute("message", "Cập nhật thất bại!");
			model.addAttribute("orderDetail", orderDetail);
		}

		return "redirect:/admin/orders";
	}

	// delete order
	@GetMapping("/deleteOrder/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, Model model) {
		OrderDetail orderDetail = orderDetailRepository.getById(id);
		int orderId = orderDetail.getOrder().getOrderId();

		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
		orderDetailRepository.deleteById(id);

		if (orderDetails.size() == 1){
			orderRepository.deleteById(orderDetail.getOrder().getOrderId());
		}else {
			Order order = orderRepository.findById(orderId).orElse(null);
			Double sumTotalPrice = orderDetailRepository.sumTotalPrice(orderId);
			order.setTotalPrice(sumTotalPrice);
			orderRepository.save(order);
		}

		model.addAttribute("message", "Delete successful!");
		return "redirect:/admin/orders";
	}

	// to excel
	@GetMapping(value = "/export")
	public void exportToExcel(HttpServletResponse response) throws IOException {

		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachement; filename=orders.xlsx";

		response.setHeader(headerKey, headerValue);

		List<Order> lisOrders = orderDetailService.listAll();

		OrderExcelExporter excelExporter = new OrderExcelExporter(lisOrders);
		excelExporter.export(response);

	}

}
