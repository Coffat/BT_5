package com.login.baitap5.repository;

import com.login.baitap5.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Tìm kiếm theo tên
    Optional<Category> findByName(String name);
    
    // Tìm kiếm theo tên (không phân biệt hoa thường)
    Optional<Category> findByNameIgnoreCase(String name);
    
    // Tìm tất cả category đang hoạt động
    List<Category> findByStatusTrue();
    
    // Tìm kiếm category theo tên có chứa keyword (không phân biệt hoa thường)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Category> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm kiếm category theo tên có chứa keyword và status
    @Query("SELECT c FROM Category c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND c.status = :status")
    Page<Category> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") Boolean status, Pageable pageable);
    
    // Đếm số lượng video trong mỗi category
    @Query("SELECT c, COUNT(v) FROM Category c LEFT JOIN c.videos v GROUP BY c")
    List<Object[]> findCategoriesWithVideoCount();
    
    // Kiểm tra xem tên category đã tồn tại chưa (trừ category hiện tại)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    // Kiểm tra xem tên category đã tồn tại chưa
    boolean existsByNameIgnoreCase(String name);
}
