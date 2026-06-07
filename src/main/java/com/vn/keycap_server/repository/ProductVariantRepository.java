package com.vn.keycap_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ProductVariant;

import feign.Param;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("SELECT DISTINCT v FROM ProductVariant v " +
            "JOIN FETCH v.product p " +
            "LEFT JOIN FETCH v.attributes " +
            "LEFT JOIN FETCH p.images " +
            "WHERE v.id IN :ids")
    List<ProductVariant> findAllByWithProductAndAttributes(@Param("ids") List<Long> id);

}
