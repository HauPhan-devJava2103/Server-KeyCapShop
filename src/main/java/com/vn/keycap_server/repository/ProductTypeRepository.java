package com.vn.keycap_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ProductType;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
}
