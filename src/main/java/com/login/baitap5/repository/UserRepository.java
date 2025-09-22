package com.login.baitap5.repository;

import com.login.baitap5.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Tìm user theo username
    Optional<User> findByUsername(String username);
    
    // Tìm user theo email
    Optional<User> findByEmail(String email);
    
    // Tìm user theo username hoặc email
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Tìm tất cả user đang hoạt động
    List<User> findByStatusTrue();
    
    // Tìm user theo role
    List<User> findByRole(User.Role role);
    
    // Tìm user theo role và status
    List<User> findByRoleAndStatus(User.Role role, Boolean status);
    
    // Tìm kiếm user theo keyword (username, email, fullName)
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm kiếm user theo keyword và role
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "u.role = :role")
    Page<User> findByKeywordAndRole(@Param("keyword") String keyword, @Param("role") User.Role role, Pageable pageable);
    
    // Tìm kiếm user theo keyword và status
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "u.status = :status")
    Page<User> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") Boolean status, Pageable pageable);
    
    // Đếm số lượng video của mỗi user
    @Query("SELECT u, COUNT(v) FROM User u LEFT JOIN u.videos v GROUP BY u")
    List<Object[]> findUsersWithVideoCount();
    
    // Kiểm tra username đã tồn tại chưa (trừ user hiện tại)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username) AND u.id != :id")
    boolean existsByUsernameIgnoreCaseAndIdNot(@Param("username") String username, @Param("id") Long id);
    
    // Kiểm tra email đã tồn tại chưa (trừ user hiện tại)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.id != :id")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsernameIgnoreCase(String username);
    
    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmailIgnoreCase(String email);
}
