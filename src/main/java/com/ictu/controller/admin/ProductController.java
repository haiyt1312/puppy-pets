package com.ictu.controller.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ictu.controller.CommonController;
import com.ictu.entity.Category;
import com.ictu.entity.Product;
import com.ictu.entity.Supplier;
import com.ictu.repository.CategoryRepository;
import com.ictu.repository.ProductRepository;
import com.ictu.repository.SuppliersRepository;

@Controller
public class ProductController extends CommonController {

	@Value("${upload.path}")
	private String pathUploadImage;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	SuppliersRepository suppliersRepository;

	public ProductController(CategoryRepository categoryRepository, SuppliersRepository suppliersRepository,
			ProductRepository productRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.suppliersRepository = suppliersRepository;
	}

	// show list product - table list
	@ModelAttribute("products")
	public List<Product> showProduct(Model model) {
		List<Product> products = productRepository.findAll();
		model.addAttribute("products", products);

		return products;
	}

	@GetMapping(value = "/admin/products")
	public String products(Model model, Principal principal) {
		Product product = new Product();
		model.addAttribute("product", product);

		return "admin/products";
	}

	// add product
	@PostMapping(value = "/addProduct")
	public String addProduct(@ModelAttribute("product") Product product, ModelMap model,
			@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {

		try {
			File convFile = new File(pathUploadImage + "/" + file.getOriginalFilename());
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {

		}

		product.setImage(file.getOriginalFilename());
		Product p = productRepository.save(product);
		if (null != p) {
			model.addAttribute("message", "Update success");
			model.addAttribute("product", product);
		} else {
			model.addAttribute("message", "Update fail");
			model.addAttribute("product", product);
		}
		return "redirect:/admin/products";
	}

	// show select option ở add product
	@ModelAttribute("categoryList")
	public List<Category> showCategory(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		return categoryList;
	}

	// show select option ở add product
	@ModelAttribute("supplierList")
	public List<Supplier> supplierList(Model model) {
		List<Supplier> supplierList = suppliersRepository.findAll();
		model.addAttribute("supplierList", supplierList);

		return supplierList;
	}

	// get Edit product
	@GetMapping(value = "/editProduct/{id}")
	public String editProduct(@PathVariable("id") Integer id, ModelMap model) {
		Product product = productRepository.findById(id).orElse(null);

		model.addAttribute("product", product);

		return "admin/editProduct";
	}

	// delete product
	@GetMapping("/deleteProduct/{id}")
	public String delProduct(@PathVariable("id") Integer id, Model model) {
		
		try {
			productRepository.deleteById(id);
			model.addAttribute("message", "Delete successful!");
		} catch (Exception e) {
			return "site/notFound";
		}
		

		return "redirect:/admin/products";
	}

}
