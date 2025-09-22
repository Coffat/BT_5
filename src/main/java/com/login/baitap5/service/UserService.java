package com.login.baitap5.service;

import com.login.baitap5.entity.User;
import com.login.baitap5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Lấy tất cả users
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    // Lấy users với phân trang
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    // Tìm user theo ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // Tìm user theo username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Tìm user theo email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Lấy tất cả users đang hoạt động
    public List<User> findActiveUsers() {
        return userRepository.findByStatusTrue();
    }
    
    // Lấy users theo role
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    // Lấy users theo role và status
    public List<User> findByRoleAndStatus(User.Role role, Boolean status) {
        return userRepository.findByRoleAndStatus(role, status);
    }
    
    // Tìm kiếm users theo keyword
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByKeyword(keyword.trim(), pageable);
    }
    
    // Tìm kiếm users theo keyword và role
    public Page<User> searchUsers(String keyword, User.Role role, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByKeywordAndRole(keyword.trim(), role, pageable);
    }
    
    // Tìm kiếm users theo keyword và status
    public Page<User> searchUsers(String keyword, Boolean status, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByKeywordAndStatus(keyword.trim(), status, pageable);
    }
    
    // Lưu user mới
    public User save(User user) {
        // Mã hóa mật khẩu trước khi lưu
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    // Cập nhật user
    public User update(User user) {
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            
            // Cập nhật thông tin
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setFullName(user.getFullName());
            existingUser.setPhone(user.getPhone());
            existingUser.setRole(user.getRole());
            existingUser.setStatus(user.getStatus());
            
            // Chỉ cập nhật mật khẩu nếu có mật khẩu mới
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            return userRepository.save(existingUser);
        }
        return null;
    }
    
    // Thay đổi mật khẩu
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    // Reset mật khẩu
    public void resetPassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
    
    // Xóa user theo ID
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    // Xóa mềm user (chỉ thay đổi status)
    public void softDelete(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(false);
            userRepository.save(user);
        }
    }
    
    // Khôi phục user
    public void restore(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(true);
            userRepository.save(user);
        }
    }
    
    // Kiểm tra username đã tồn tại chưa
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
    
    // Kiểm tra email đã tồn tại chưa
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
    
    // Kiểm tra username đã tồn tại chưa (trừ user hiện tại)
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id);
    }
    
    // Kiểm tra email đã tồn tại chưa (trừ user hiện tại)
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }
    
    // Đếm tổng số users
    public long count() {
        return userRepository.count();
    }
    
    // Lấy users với số lượng video
    public List<Object[]> getUsersWithVideoCount() {
        return userRepository.findUsersWithVideoCount();
    }
    
    // Validate user trước khi lưu
    public boolean validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra username và email đã tồn tại chưa
        if (user.getId() == null) {
            return !existsByUsername(user.getUsername()) && !existsByEmail(user.getEmail());
        } else {
            return !existsByUsernameAndIdNot(user.getUsername(), user.getId()) && 
                   !existsByEmailAndIdNot(user.getEmail(), user.getId());
        }
    }
    
    // Xác thực đăng nhập
    public User authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(username, username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getStatus() && passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}
