package com.vn.keycap_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ReviewReply;

import java.util.Optional;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    Optional<ReviewReply> findByReviewId(Long reviewId);
}
