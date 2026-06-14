package com.vn.keycap_server.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findAllByPublicIdIn(Collection<String> publicIds);
}
