package com.vn.keycap_server.service.adminstaff;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vn.keycap_server.dto.request.staff.AdminStaffListRequest;
import com.vn.keycap_server.dto.request.staff.CreateAdminStaffRequest;
import com.vn.keycap_server.dto.request.staff.UpdateAdminStaffRequest;
import com.vn.keycap_server.dto.response.staff.AdminStaffResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.AdminStaffMapper;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.service.mail.IMailService;
import com.vn.keycap_server.utils.ERole;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ quản lý nhân viên cho admin.
 * Nhân viên được lưu bằng entity User với role STAFF để tái sử dụng hệ thống
 * đăng nhập hiện có.
 */
@Service
@RequiredArgsConstructor
public class AdminStaffService implements IAdminStaffService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AdminStaffMapper adminStaffMapper;
    private final PasswordEncoder passwordEncoder;
    private final IMailService mailService;

    /**
     * Lấy danh sách nhân viên có phân trang và tìm kiếm theo tên, email hoặc số
     * điện thoại.
     *
     * @param request query params từ FE admin
     * @return trang dữ liệu nhân viên
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdminStaffResponse> getStaffs(AdminStaffListRequest request) {
        // 1. Chuẩn hóa request và sắp xếp nhân viên mới tạo trước
        String search = normalizeSearch(request.getSearch());
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getLimit(),
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));

        // 2. Chỉ truy vấn user có role STAFF, tuyệt đối không lộ ADMIN hoặc USER thường
        // ra API này.
        Page<User> staffPage = StringUtils.hasText(search)
                ? userRepository.searchByRoleAndKeyword(ERole.STAFF, search, pageable)
                : userRepository.findByRole(ERole.STAFF, pageable);

        // 3. Map entity sang DTO, không trả entity trực tiếp ra ngoài.
        return staffPage.map(adminStaffMapper::toAdminStaffResponse);
    }

    /**
     * Lấy chi tiết một nhân viên theo ID.
     *
     * @param staffId ID nhân viên cần xem
     * @return thông tin nhân viên
     */
    @Override
    @Transactional(readOnly = true)
    public AdminStaffResponse getStaffById(Long staffId) {
        // 1. Validate ID và load đúng nhân viên role STAFF.
        User staff = getStaffEntity(staffId);

        // 2. Map sang response
        return adminStaffMapper.toAdminStaffResponse(staff);
    }

    /**
     * Tạo tài khoản nhân viên mới.
     * API này không nhận password từ FE; backend sinh mật khẩu ngẫu nhiên để tránh
     * lộ mật khẩu mặc định.
     *
     * @param request payload tạo nhân viên từ FE
     * @return nhân viên vừa tạo
     */
    @Override
    @Transactional
    public AdminStaffResponse createStaff(CreateAdminStaffRequest request) {
        // 1. Chuẩn hóa dữ liệu đầu vào trước khi kiểm tra trùng lặp.
        String email = normalizeEmail(request.getEmail());
        String fullName = normalizeRequiredText(request.getName(), "Tên nhân viên không được để trống");
        String phone = normalizeRequiredText(request.getPhonenumber(), "Số điện thoại không được để trống");
        BigDecimal salary = normalizeSalary(request.getSalary());

        // 2. Email là định danh đăng nhập nên không được trùng với bất kỳ user nào.
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã tồn tại trong hệ thống");
        }

        // 3. Tạo user role STAFF và set mật khẩu ngẫu nhiên đã mã hóa.
        String temporaryPassword = generateTemporaryPassword();
        User staff = User.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .dateOfBirth(request.getDob())
                .gender(request.getGender())
                .salary(salary)
                .password(passwordEncoder.encode(temporaryPassword))
                .role(ERole.STAFF)
                .build();

        // 4. Lưu nhân viên trước, sau đó gửi email để nhân viên có thông tin đăng nhập.
        User savedStaff = userRepository.save(staff);
        mailService.sendStaffAccountEmail(savedStaff.getEmail(), savedStaff.getFullName(), temporaryPassword);

        // 5. Trả response đúng field FE đang dùng.
        return adminStaffMapper.toAdminStaffResponse(savedStaff);
    }

    /**
     * Cập nhật thông tin nhân viên.
     * Không cho cập nhật role hoặc password qua API này để giới hạn bề mặt tấn
     * công.
     *
     * @param staffId ID nhân viên cần cập nhật
     * @param request payload cập nhật
     * @return nhân viên sau cập nhật
     */
    @Override
    @Transactional
    public AdminStaffResponse updateStaff(Long staffId, UpdateAdminStaffRequest request) {
        // 1. Load đúng nhân viên role STAFF, tránh cập nhật nhầm admin hoặc user
        // thường.
        User staff = getStaffEntity(staffId);

        // 2. Cập nhật từng field nếu FE gửi lên.
        if (request.getName() != null) {
            staff.setFullName(normalizeRequiredText(request.getName(), "Tên nhân viên không được để trống"));
        }

        if (request.getEmail() != null) {
            String email = normalizeEmail(request.getEmail());
            if (userRepository.existsByEmailAndIdNot(email, staffId)) {
                throw new BadRequestException("Email đã tồn tại trong hệ thống");
            }
            staff.setEmail(email);
        }

        if (request.getPhonenumber() != null) {
            staff.setPhone(normalizeRequiredText(request.getPhonenumber(), "Số điện thoại không được để trống"));
        }

        if (request.getDob() != null) {
            staff.setDateOfBirth(request.getDob());
        }

        if (request.getGender() != null) {
            staff.setGender(request.getGender());
        }

        if (request.getSalary() != null) {
            staff.setSalary(normalizeSalary(request.getSalary()));
        }

        // 3. Lưu thay đổi và trả DTO; không trả entity trực tiếp.
        User savedStaff = userRepository.save(staff);
        return adminStaffMapper.toAdminStaffResponse(savedStaff);
    }

    /**
     * Xóa nhân viên theo ID.
     * Service chặn tự xóa chính mình và chặn xóa tài khoản đã phát sinh đơn hàng để
     * tránh lỗi khóa ngoại.
     *
     * @param staffId     ID nhân viên cần xóa
     * @param actorUserId ID admin đang thao tác lấy từ JWT
     */
    @Override
    @Transactional
    public void deleteStaff(Long staffId, Long actorUserId) {
        // 1. Validate và load nhân viên role STAFF.
        User staff = getStaffEntity(staffId);

        // 2. Chặn tự xóa chính mình nếu tài khoản thao tác cũng là staff/admin cùng ID.
        if (actorUserId != null && staffId.equals(actorUserId)) {
            throw new BadRequestException("Không thể tự xóa tài khoản đang đăng nhập");
        }

        // 3. Xóa user staff; token/address sẽ được dọn theo cascade hiện có trên User.
        userRepository.delete(staff);
    }

    /**
     * Lấy entity nhân viên và đảm bảo ID hợp lệ.
     */
    private User getStaffEntity(Long staffId) {
        // 1. ID phải là số dương.
        if (staffId == null || staffId <= 0) {
            throw new BadRequestException("ID nhân viên không hợp lệ");
        }

        // 2. Chỉ tìm trong role STAFF để API này không nhầm ADMIN hoặc USER.
        return userRepository.findByIdAndRole(staffId, ERole.STAFF)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + staffId));
    }

    /**
     * Chuẩn hóa từ khóa tìm kiếm.
     */
    private String normalizeSearch(String search) {
        // 1. Chuỗi rỗng được coi là không tìm kiếm.
        return StringUtils.hasText(search) ? search.trim() : null;
    }

    /**
     * Chuẩn hóa email trước khi lưu và tìm kiếm trùng.
     */
    private String normalizeEmail(String email) {
        // 1. Email đã được @Valid kiểm tra format, service chỉ trim và đưa về
        // lowercase.
        return email.trim().toLowerCase();
    }

    /**
     * Chuẩn hóa text bắt buộc, dùng thêm trong PATCH vì field nullable.
     */
    private String normalizeRequiredText(String value, String message) {
        // 1. Nếu FE gửi chuỗi rỗng trong PATCH thì coi là dữ liệu không hợp lệ.
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }

    /**
     * Chuẩn hóa mức lương và bảo vệ thêm ở tầng service.
     */
    private BigDecimal normalizeSalary(BigDecimal salary) {
        // 1. DTO đã validate, service vẫn giữ guard để tránh dữ liệu âm đi vào DB từ
        // nơi gọi khác.
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Mức lương không hợp lệ");
        }
        return salary;
    }

    /**
     * Sinh mật khẩu tạm thời ngẫu nhiên cho nhân viên mới.
     */
    private String generateTemporaryPassword() {
        // 1. Không dùng mật khẩu mặc định cố định; chuỗi này chỉ lưu dạng hash và không
        // trả về API.
        return UUID.randomUUID() + "Aa1!" + SECURE_RANDOM.nextInt(1000);
    }
}
