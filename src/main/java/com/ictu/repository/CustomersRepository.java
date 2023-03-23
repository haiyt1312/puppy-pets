package com.ictu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ictu.entity.Customer;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Integer> {

	@Query(value = "select * from customers where customer_id = ?", nativeQuery = true)
	public Customer findCustomersLogin(String customerId);

	@Query(value = "select * from customers where email = ?", nativeQuery = true)
	Optional<Customer> FindByEmail(String email);

	@Query(value = "with tb1 as (select count(c.customer_id) 'total_customer'\n" +
			"\t\t\tfrom customers c),\n" +
			"tb2 as (select count(DISTINCT od.order_id) 'successful_order'\n" +
			"\t\tfrom order_details od\n" +
			"        where od.status = 'Đã Thanh Toán'),\n" +
			"tb3 as (select count(DISTINCT od.order_id) 'orders_shipping'\n" +
			"\t\tfrom order_details od\n" +
			"        where od.status = 'Đang Vận Chuyển'),\n" +
			"tb4 as (select sum(od.total_price) 'total_price'\n" +
			"\t\tfrom order_details od\n" +
			"        where od.status = 'Đã Thanh Toán')\n" +
			"select total_customer, successful_order, orders_shipping, total_price\n" +
			"from tb1, tb2, tb3, tb4", nativeQuery = true)
	public List<Object[]> getDashboard();
}
