package com.vn.keycap_server.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findAllByPublicIdIn(Collection<String> publicIds);

    /**
     * Tìm media theo ID và owner để user không thể sử dụng media của người khác.
     */
    Optional<Media> findByIdAndUploadedById(Long id, Long userId);

    /**
     * Tìm danh sách media theo secure URL.
     * Dùng khi FE chỉ gửi URL ảnh trong request tạo/cập nhật sản phẩm.
     */
    List<Media> findAllBySecureUrlIn(Collection<String> secureUrls);
}
