package com.vn.keycap_server.service.user;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vn.keycap_server.dto.request.user.UpdateProfileRequest;
import com.vn.keycap_server.dto.response.user.ProfileStatsResponse;
import com.vn.keycap_server.dto.response.user.UserProfileResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.UserProfileMapper;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.MediaRepository;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.utils.EMediaResourceType;
import com.vn.keycap_server.utils.EMediaStatus;
import com.vn.keycap_server.utils.EOrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final UserProfileMapper userProfileMapper;
    private final Cloudinary cloudinary;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findProfileById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        return buildProfileResponse(user, userId);
    }

    /**
     * Cập nhật thông tin profile và chỉ kích hoạt avatar thuộc sở hữu của user hiện
     * tại.
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request, Long userId) {
        User user = userRepository.findProfileById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        user.setFullName(request.getFullName().trim());
        user.setPhone(normalizePhone(request.getPhone()));

        if (request.getAvatarMediaId() != null) {
            Media avatarMedia = getOwnedAvatarMedia(request.getAvatarMediaId(), userId);
            avatarMedia.setStatus(EMediaStatus.ACTIVE);
            user.setAvatarMedia(avatarMedia);

            // Chỉ bỏ tag tmp sau khi transaction DB commit để Cloudinary không lệch trạng
            // thái với DB.
            removeTemporaryTagAfterCommit(avatarMedia);
        }

        userRepository.save(user);
        return buildProfileResponse(user, userId);
    }

    /**
     * Tạo response profile dùng chung cho API đọc và cập nhật hồ sơ.
     */
    private UserProfileResponse buildProfileResponse(User user, Long userId) {
        UserProfileResponse response = userProfileMapper.toUserProfileResponse(user);

        // Chỉ truy vấn số lượng cần hiển thị, không tải toàn bộ đơn hàng và wishlist
        // vào bộ nhớ.
        response.setStats(ProfileStatsResponse.builder()
                .totalOrders(orderRepository.countByUserId(userId))
                .completedOrders(orderRepository.countByUserIdAndStatus(userId, EOrderStatus.SUCCESS))
                .wishlistItems(wishlistRepository.countByUserId(userId))
                .build());

        return response;
    }

    /**
     * Kiểm tra media avatar tồn tại, thuộc user hiện tại và đúng loại ảnh.
     */
    private Media getOwnedAvatarMedia(Long mediaId, Long userId) {
        Media media = mediaRepository.findByIdAndUploadedById(mediaId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy media thuộc người dùng"));

        if (media.getResourceType() != EMediaResourceType.IMAGE) {
            throw new BadRequestException("Avatar chỉ chấp nhận media loại ảnh");
        }
        return media;
    }

    /**
     * Đăng ký thao tác bỏ tag tmp sau khi database đã commit thành công.
     */
    private void removeTemporaryTagAfterCommit(Media media) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            /**
             * Bỏ tag tạm khi transaction database đã hoàn tất thành công.
             */
            @Override
            public void afterCommit() {
                try {
                    cloudinary.uploader().removeTag(
                            "tmp",
                            new String[] { media.getPublicId() },
                            ObjectUtils.asMap("resource_type", media.getResourceType().name().toLowerCase()));
                } catch (Exception exception) {
                    // Profile đã lưu thành công nên lỗi Cloudinary chỉ được log để xử lý lại sau.
                    log.warn("Không thể bỏ tag tmp cho media publicId={}: {}",
                            media.getPublicId(), exception.getMessage());
                }
            }
        });
    }

    /**
     * Chuẩn hóa số điện thoại rỗng thành null trước khi lưu database.
     */
    private String normalizePhone(@Nullable String phone) {
        return phone == null || phone.isBlank() ? null : phone.trim();
    }
}
