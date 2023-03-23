package com.ictu.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ictu.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	// Hiển thị danh sách product mới nhất ở trang chủ LIMIT = 10
	@Query(value = "SELECT * FROM products ORDER BY entered_date DESC limit 10", nativeQuery = true)
	public List<Product> listProduct10();

	// Hiển thị Top 10 sản phẩm bán chạy nhất
	@Query(value = "SELECT p.product_id, COUNT(*) AS SoLuong FROM order_details p JOIN products c ON p.product_id = c.product_id GROUP BY p.product_id ORDER by SoLuong DESC limit 10;", nativeQuery = true)
	public List<Product> topSellingProduct10();

	// Hiển thị Top 10 sản phẩm bán chạy nhất
	@Query(value = "select product_id, max(discount) AS KM from products GROUP BY product_id  ORDER by KM DESC limit 10;", nativeQuery = true)
	public List<Product> topDiscount10();

	// Cart Item
	@Query(value = "SELECT * FROM products where product_id = ?", nativeQuery = true)
	public Product findByIdProduct(int productId);

	// List Sản phẩm by danh mục
	@Query(value = "SELECT * FROM products WHERE category_id = ?", nativeQuery = true)
	public List<Product> listProductByCategory(Integer categoryId);

	@Query(value = "select * from products where category_id = ?", nativeQuery = true)
	Page<Product> findAllProductByCategoryId(Integer id, Pageable pageable);

	// List Sản phẩm by nhà cung cấp
	@Query(value = "SELECT * FROM products where supplier_id = ?", nativeQuery = true)
	public List<Product> listProductBySupplier(Integer supplierId);

	// Search Product
	@Query(value = "SELECT * FROM products WHERE name LIKE %?1%", nativeQuery = true)
	public List<Product> searchProduct(String name);

	// Gợi ý sản phẩm cùng thể loại
	@Query(value = "SELECT * FROM products WHERE category_id = ?;", nativeQuery = true)
	List<Product> productsByCategory(Integer categoryId);

}
