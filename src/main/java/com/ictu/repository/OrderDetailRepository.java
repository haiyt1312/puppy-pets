package com.ictu.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ictu.entity.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

	@Query(value = "select * from order_details where order_id = ?", nativeQuery = true)
	List<OrderDetail> findByOrderId(int id);

	// Thống kê theo sản phẩm được bán ra
	@Query(value = "SELECT p.name ,  \r\n" + "SUM(o.quantity) as quantity ,\r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg,\r\n"
			+ "Min(o.total_price) as min, \r\n" + "max(o.total_price) as max\r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN products p ON o.product_id = p.product_id\r\n" + "GROUP BY p.name;", nativeQuery = true)

	public List<Object[]> repo();

	// Thống kê theo thể loại được bán ra
	@Query(value = "SELECT c.name , \r\n" + "SUM(o.quantity) as quantity ,\r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg, \r\n"
			+ "Min(o.total_price) as min,\r\n" + "max(o.total_price) as max \r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN products p ON o.product_id = p.product_id\r\n"
			+ "INNER JOIN categories c ON p.category_id = c.category_id\r\n" + "GROUP BY c.name;", nativeQuery = true)

	public List<Object[]> repoWhereCategory();

	// Thống kê các sp từ thương hiệu được bán ra
	@Query(value = "SELECT s.name , \r\n" + "SUM(o.quantity) as quantity ,\r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg  ,\r\n"
			+ "Min(o.total_price) as min  ,\r\n" + "max(o.total_price) as max \r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN products p ON o.product_id = p.product_id\r\n"
			+ "INNER JOIN suppliers s ON p.supplier_id = s.id\r\n" + "GROUP BY s.name;", nativeQuery = true)

	public List<Object[]> repoWhereSuppliers();

	// Thống kê sản phẩm theo năm // theo các năm
	@Query(value = "Select YEAR(od.order_date) ,\r\n" + "SUM(o.quantity) as quantity ,\r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg  ,\r\n"
			+ "Min(o.total_price) as min  ,\r\n" + "max(o.total_price) as max \r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN orders od ON o.order_id =od.order_id\r\n"
			+ "GROUP BY YEAR(od.order_date);", nativeQuery = true)
	public List<Object[]> repoWhereYear();

	// Thống kê sản phẩm theo tháng // theo các Tháng
	@Query(value = "Select month(od.order_date) ,\r\n" + "SUM(o.quantity) as quantity ,    \r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg  ,\r\n"
			+ "Min(o.total_price) as min  ,\r\n" + "max(o.total_price) as max\r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN orders od ON o.order_id =od.order_id\r\n"
			+ "GROUP BY month(od.order_date);", nativeQuery = true)

	public List<Object[]> repoWhereMonth();

	// Thống kê sản phẩm theo quý // theo các quý
	@Query(value = "Select QUARTER(od.order_date),\r\n" + "SUM(o.quantity) as quantity , \r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg, \r\n"
			+ "Min(o.total_price) as min,\r\n" + "max(o.total_price) as max\r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN orders od ON o.order_id =od.order_id\r\n"
			+ "GROUP By QUARTER(od.order_date);", nativeQuery = true)

	public List<Object[]> repoWhereQUARTER();

	// Thống kê sản phẩm theo người đặt hàng
	@Query(value = "SELECT c.customer_id,\r\n" + "SUM(o.quantity) as quantity,  \r\n"
			+ "SUM(o.quantity * o.total_price) as sum,\r\n" + "AVG(o.total_price) as avg,\r\n"
			+ "Min(o.total_price) as min, \r\n" + "max(o.total_price) as max \r\n" + "FROM order_details o\r\n"
			+ "INNER JOIN orders p ON o.order_id = p.order_id\r\n"
			+ "INNER JOIN customers c ON p.customer_id = c.customer_id\r\n"
			+ "GROUP BY c.customer_id;", nativeQuery = true)
	public List<Object[]> reportCustommer();

	@Query(value = "with tb1 as(select SUM(o.quantity * o.total_price) as month_1 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 1 and year(od.order_date) = :year),\n" +
			"tb2 as(select SUM(o.quantity * o.total_price) as month_2 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 2 and year(od.order_date) = :year),\n" +
			"tb3 as(select SUM(o.quantity * o.total_price) as month_3 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 3 and year(od.order_date) = :year),\n" +
			"tb4 as(select SUM(o.quantity * o.total_price) as month_4 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 4 and year(od.order_date) = :year),\n" +
			"tb5 as(select SUM(o.quantity * o.total_price) as month_5 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 5 and year(od.order_date) = :year),\n" +
			"tb6 as(select SUM(o.quantity * o.total_price) as month_6 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 6 and year(od.order_date) = :year),\n" +
			"tb7 as(select SUM(o.quantity * o.total_price) as month_7 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 7 and year(od.order_date) = :year),\n" +
			"tb8 as(select SUM(o.quantity * o.total_price) as month_8 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 8 and year(od.order_date) = :year),\n" +
			"tb9 as(select SUM(o.quantity * o.total_price) as month_9 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 9 and year(od.order_date) = :year),\n" +
			"tb10 as(select SUM(o.quantity * o.total_price) as month_10 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 10 and year(od.order_date) = :year),\n" +
			"tb11 as(select SUM(o.quantity * o.total_price) as month_11 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 11 and year(od.order_date) = :year),\n" +
			"tb12 as(select SUM(o.quantity * o.total_price) as month_12 FROM order_details o INNER JOIN orders od ON o.order_id =od.order_id where o.status = 'Đã Thanh Toán' and month(od.order_date) = 12 and year(od.order_date) = :year)\n" +
			"select month_1, month_2, month_3, month_4, month_5, month_6, month_7, month_8, month_9, month_10, month_11, month_12\n" +
			"from tb1, tb2, tb3, tb4, tb5, tb6, tb7, tb8, tb9, tb10, tb11, tb12;", nativeQuery = true)
	public List<Object[]> repoMonth(Date year);
	
	@Query(value = "with tb1 as (select count(o.quantity) as quantity_1 from order_details o left join products p on o.product_id = p.product_id left join categories c on c.category_id = p.category_id where c.category_id = 1),\r\n"
			+ "tb2 as (select count(o.quantity) as quantity_2 from order_details o left join products p on o.product_id = p.product_id left join categories c on c.category_id = p.category_id where c.category_id = 2),\r\n"
			+ "tb3 as (select count(o.quantity) as quantity_3 from order_details o left join products p on o.product_id = p.product_id left join categories c on c.category_id = p.category_id where c.category_id = 3),\r\n"
			+ "tb4 as (select count(o.quantity) as quantity_4 from order_details o left join products p on o.product_id = p.product_id left join categories c on c.category_id = p.category_id where c.category_id = 4)\r\n"
			+ "select quantity_1, quantity_2, quantity_3, quantity_4\r\n"
			+ "from tb1, tb2, tb3, tb4", nativeQuery = true)
	public List<Object[]> repoCategory();
}
