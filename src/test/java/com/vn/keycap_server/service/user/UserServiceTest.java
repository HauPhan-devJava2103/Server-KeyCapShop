package com.vn.keycap_server.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vn.keycap_server.dto.response.user.UserProfileResponse;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.UserProfileMapper;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.utils.EOrderStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, orderRepository, wishlistRepository, userProfileMapper);
    }

    @Test
    void getProfileReturnsAvatarMediaAndShoppingStats() {
        Media avatarMedia = Media.builder()
                .secureUrl("https://res.cloudinary.com/keycap-shop/avatar.webp")
                .build();
        User user = User.builder()
                .email("user@example.com")
                .fullName("Nguyễn Văn A")
                .avatarMedia(avatarMedia)
                .build();
        user.setId(7L);
        UserProfileResponse mappedResponse = UserProfileResponse.builder()
                .id(7L)
                .avatar(avatarMedia.getSecureUrl())
                .build();

        when(userRepository.findProfileById(7L)).thenReturn(Optional.of(user));
        when(userProfileMapper.toUserProfileResponse(user)).thenReturn(mappedResponse);
        when(orderRepository.countByUserId(7L)).thenReturn(12L);
        when(orderRepository.countByUserIdAndStatus(7L, EOrderStatus.DELIVERED)).thenReturn(10L);
        when(wishlistRepository.countByUserId(7L)).thenReturn(28L);

        UserProfileResponse response = userService.getProfile(7L);

        assertThat(response.getAvatar()).isEqualTo(avatarMedia.getSecureUrl());
        assertThat(response.getStats().getTotalOrders()).isEqualTo(12L);
        assertThat(response.getStats().getCompletedOrders()).isEqualTo(10L);
        assertThat(response.getStats().getWishlistItems()).isEqualTo(28L);
        verify(orderRepository).countByUserIdAndStatus(7L, EOrderStatus.DELIVERED);
    }

    @Test
    void getProfileRejectsUnknownUser() {
        when(userRepository.findProfileById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy người dùng");
    }
}
