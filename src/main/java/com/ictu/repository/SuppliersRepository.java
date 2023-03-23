package com.ictu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictu.entity.Supplier;

@Repository
public interface SuppliersRepository extends JpaRepository<Supplier, Integer> {

}
