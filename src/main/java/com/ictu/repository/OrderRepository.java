package com.ictu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ictu.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query(value = "select * from orders where customer_id = ?1", nativeQuery = true)
	List<Order> findByCustomerId(String id);

	@Query(value = "select o.order_id, o.address, o.order_date, od.total_price, od.status, p.name, od.quantity "
			+ "from orders o " + "left join order_details od on o.order_id = od.order_id "
			+ "left join products p on od.product_id = p.product_id " + "where o.customer_id = ?", nativeQuery = true)
	public List<Object[]> orderByCustomerId(String id);

	@Modifying
	@Query(value = "update orders o set o.address :address where o.order_id :orderId", nativeQuery = true)
	void updateOrderById(String address, Integer orderId);
}
