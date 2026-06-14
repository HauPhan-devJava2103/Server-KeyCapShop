package com.vn.keycap_server.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.response.user.ProfileStatsResponse;
import com.vn.keycap_server.dto.response.user.UserProfileResponse;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.UserProfileMapper;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.utils.EOrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findProfileById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        UserProfileResponse response = userProfileMapper.toUserProfileResponse(user);

        // Chỉ truy vấn số lượng cần hiển thị, không tải toàn bộ đơn hàng và wishlist vào bộ nhớ.
        response.setStats(ProfileStatsResponse.builder()
                .totalOrders(orderRepository.countByUserId(userId))
                .completedOrders(orderRepository.countByUserIdAndStatus(userId, EOrderStatus.DELIVERED))
                .wishlistItems(wishlistRepository.countByUserId(userId))
                .build());

        return response;
    }
}
