package com.login.baitap5.repository;

import com.login.baitap5.entity.Video;
import com.login.baitap5.entity.Category;
import com.login.baitap5.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    // Tìm video theo category
    List<Video> findByCategory(Category category);
    
    // Tìm video theo category với phân trang
    Page<Video> findByCategory(Category category, Pageable pageable);
    
    // Tìm video theo user
    List<Video> findByUser(User user);
    
    // Tìm video theo user với phân trang
    Page<Video> findByUser(User user, Pageable pageable);
    
    // Tìm tất cả video đang hoạt động
    List<Video> findByStatusTrue();
    
    // Tìm video đang hoạt động với phân trang
    Page<Video> findByStatusTrue(Pageable pageable);
    
    // Tìm kiếm video theo keyword (title, description)
    @Query("SELECT v FROM Video v WHERE " +
           "LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Video> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm kiếm video theo keyword và category
    @Query("SELECT v FROM Video v WHERE " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "v.category = :category")
    Page<Video> findByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") Category category, Pageable pageable);
    
    // Tìm kiếm video theo keyword và user
    @Query("SELECT v FROM Video v WHERE " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "v.user = :user")
    Page<Video> findByKeywordAndUser(@Param("keyword") String keyword, @Param("user") User user, Pageable pageable);
    
    // Tìm kiếm video theo keyword và status
    @Query("SELECT v FROM Video v WHERE " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "v.status = :status")
    Page<Video> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") Boolean status, Pageable pageable);
    
    // Tìm video phổ biến nhất (theo views)
    @Query("SELECT v FROM Video v WHERE v.status = true ORDER BY v.views DESC")
    Page<Video> findMostPopularVideos(Pageable pageable);
    
    // Tìm video mới nhất
    @Query("SELECT v FROM Video v WHERE v.status = true ORDER BY v.createdDate DESC")
    Page<Video> findNewestVideos(Pageable pageable);
    
    // Tìm video được like nhiều nhất
    @Query("SELECT v FROM Video v WHERE v.status = true ORDER BY v.likes DESC")
    Page<Video> findMostLikedVideos(Pageable pageable);
    
    // Đếm số video theo category
    @Query("SELECT COUNT(v) FROM Video v WHERE v.category = :category AND v.status = true")
    Long countByCategoryAndStatusTrue(@Param("category") Category category);
    
    // Đếm số video theo user
    @Query("SELECT COUNT(v) FROM Video v WHERE v.user = :user AND v.status = true")
    Long countByUserAndStatusTrue(@Param("user") User user);
    
    // Tìm video liên quan (cùng category, khác video hiện tại)
    @Query("SELECT v FROM Video v WHERE v.category = :category AND v.id != :videoId AND v.status = true ORDER BY v.createdDate DESC")
    Page<Video> findRelatedVideos(@Param("category") Category category, @Param("videoId") Long videoId, Pageable pageable);
}
