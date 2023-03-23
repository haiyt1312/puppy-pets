package com.ictu.controller.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ictu.controller.CommonController;
import com.ictu.entity.Supplier;
import com.ictu.repository.SuppliersRepository;

@Controller
public class SuppliersController extends CommonController {

	@Autowired
	SuppliersRepository suppliersRepository;

//	@Autowired
//	CustomersRepository customersRepository;

	// show list supplier - table list
	@ModelAttribute("suppliers")
	public List<Supplier> showSupplier(Model model) {
		List<Supplier> suppliers = suppliersRepository.findAll();
		model.addAttribute("suppliers", suppliers);

		return suppliers;
	}

	@GetMapping(value = "/admin/suppliers")
	public String suppliers(Model model, Principal principal) {
		Supplier supplier = new Supplier();
		model.addAttribute("supplier", supplier);

		return "admin/suppliers";
	}

	// add supplier
	@PostMapping(value = "/addSupplier")
	public String addSupplier(@Validated @ModelAttribute("supplier") Supplier supplier, ModelMap model,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "failure");

			return "admin/suppliers";
		}

		suppliersRepository.save(supplier);
		model.addAttribute("message", "successful!");

		return "redirect:/admin/suppliers";
	}

	// get Edit supplier
	@GetMapping(value = "/editSupplier/{id}")
	public String editSupplier(@PathVariable("id") Integer id, ModelMap model) {
		Supplier supplier = suppliersRepository.findById(id).orElse(null);

		model.addAttribute("supplier", supplier);

		return "admin/editSupplier";
	}

	// delete supplier
	@GetMapping("/deleteSupplier/{id}")
	public String delSupplier(@PathVariable("id") Integer id, Model model) {
		try {
			suppliersRepository.deleteById(id);
			model.addAttribute("message", "Delete successful!");
		} catch (Exception e) {
			return "site/notFound";
		}

		return "redirect:/admin/suppliers";
	}

}
