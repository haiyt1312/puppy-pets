package com.ictu.controller.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	OrderDetailRepository orderDetailRepository;

	@GetMapping(value = "/admin/orders")
	public String orders(Model model) {

		List<OrderDetail> orderDetails = orderDetailRepository.findAll();
		model.addAttribute("orderDetails", orderDetails);

		return "admin/orders";
	}

	// get edit
	@GetMapping("/editorder/{orderDetailId}")
	public String showEditOrder(@PathVariable("orderDetailId") int orderDetailId, Model model) {
		OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
				.orElseThrow(() -> new IllegalArgumentException("Id người dùng không hợp lệ:" + orderDetailId));

		model.addAttribute("orderDetail", orderDetail);

		return "admin/editOrder";
	}

	// edit order
	@RequestMapping(value = "/editorder", method = RequestMethod.POST)
	public String editordertr(@ModelAttribute("orderDetail") OrderDetail orderDetail, Model model,
			RedirectAttributes rs) {
		OrderDetail orderDetail2 = orderDetailRepository.save(orderDetail);
		if (null != orderDetail2) {
			model.addAttribute("message", "Đã xác nhận !");
			model.addAttribute("orderDetail", orderDetailRepository.findById(orderDetail2.getOrderDetailId()));
		} else {
			model.addAttribute("message", "Cập nhất thất bại !");
			model.addAttribute("orderDetail", orderDetail);
		}

		return "redirect:/admin/orders";
	}

	// delete category
	@GetMapping("/deleteOrder/{id}")
	public String delProduct(@PathVariable("id") Integer id, Model model) {
		orderDetailRepository.deleteById(id);
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
