package com.login.baitap5.service;

import com.login.baitap5.entity.Video;
import com.login.baitap5.entity.Category;
import com.login.baitap5.entity.User;
import com.login.baitap5.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VideoService {
    
    @Autowired
    private VideoRepository videoRepository;
    
    // Lấy tất cả videos
    public List<Video> findAll() {
        return videoRepository.findAll();
    }
    
    // Lấy videos với phân trang
    public Page<Video> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }
    
    // Tìm video theo ID
    public Optional<Video> findById(Long id) {
        return videoRepository.findById(id);
    }
    
    // Lấy videos theo category
    public List<Video> findByCategory(Category category) {
        return videoRepository.findByCategory(category);
    }
    
    // Lấy videos theo category với phân trang
    public Page<Video> findByCategory(Category category, Pageable pageable) {
        return videoRepository.findByCategory(category, pageable);
    }
    
    // Lấy videos theo user
    public List<Video> findByUser(User user) {
        return videoRepository.findByUser(user);
    }
    
    // Lấy videos theo user với phân trang
    public Page<Video> findByUser(User user, Pageable pageable) {
        return videoRepository.findByUser(user, pageable);
    }
    
    // Lấy tất cả videos đang hoạt động
    public List<Video> findActiveVideos() {
        return videoRepository.findByStatusTrue();
    }
    
    // Lấy videos đang hoạt động với phân trang
    public Page<Video> findActiveVideos(Pageable pageable) {
        return videoRepository.findByStatusTrue(pageable);
    }
    
    // Tìm kiếm videos theo keyword
    public Page<Video> searchVideos(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return videoRepository.findAll(pageable);
        }
        return videoRepository.findByKeyword(keyword.trim(), pageable);
    }
    
    // Tìm kiếm videos theo keyword và category
    public Page<Video> searchVideos(String keyword, Category category, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return videoRepository.findByCategory(category, pageable);
        }
        return videoRepository.findByKeywordAndCategory(keyword.trim(), category, pageable);
    }
    
    // Tìm kiếm videos theo keyword và user
    public Page<Video> searchVideos(String keyword, User user, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return videoRepository.findByUser(user, pageable);
        }
        return videoRepository.findByKeywordAndUser(keyword.trim(), user, pageable);
    }
    
    // Tìm kiếm videos theo keyword và status
    public Page<Video> searchVideos(String keyword, Boolean status, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return videoRepository.findAll(pageable);
        }
        return videoRepository.findByKeywordAndStatus(keyword.trim(), status, pageable);
    }
    
    // Lấy videos phổ biến nhất
    public Page<Video> getMostPopularVideos(Pageable pageable) {
        return videoRepository.findMostPopularVideos(pageable);
    }
    
    // Lấy videos mới nhất
    public Page<Video> getNewestVideos(Pageable pageable) {
        return videoRepository.findNewestVideos(pageable);
    }
    
    // Lấy videos được like nhiều nhất
    public Page<Video> getMostLikedVideos(Pageable pageable) {
        return videoRepository.findMostLikedVideos(pageable);
    }
    
    // Lấy videos liên quan
    public Page<Video> getRelatedVideos(Category category, Long videoId, Pageable pageable) {
        return videoRepository.findRelatedVideos(category, videoId, pageable);
    }
    
    // Lưu video
    public Video save(Video video) {
        return videoRepository.save(video);
    }
    
    // Cập nhật video
    public Video update(Video video) {
        return videoRepository.save(video);
    }
    
    // Xóa video theo ID
    public void deleteById(Long id) {
        videoRepository.deleteById(id);
    }
    
    // Xóa mềm video (chỉ thay đổi status)
    public void softDelete(Long id) {
        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setStatus(false);
            videoRepository.save(video);
        }
    }
    
    // Khôi phục video
    public void restore(Long id) {
        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setStatus(true);
            videoRepository.save(video);
        }
    }
    
    // Tăng lượt xem
    public void incrementViews(Long id) {
        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setViews(video.getViews() + 1);
            videoRepository.save(video);
        }
    }
    
    // Tăng lượt like
    public void incrementLikes(Long id) {
        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setLikes(video.getLikes() + 1);
            videoRepository.save(video);
        }
    }
    
    // Giảm lượt like
    public void decrementLikes(Long id) {
        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            if (video.getLikes() > 0) {
                video.setLikes(video.getLikes() - 1);
                videoRepository.save(video);
            }
        }
    }
    
    // Đếm tổng số videos
    public long count() {
        return videoRepository.count();
    }
    
    // Đếm số videos theo category
    public Long countByCategory(Category category) {
        return videoRepository.countByCategoryAndStatusTrue(category);
    }
    
    // Đếm số videos theo user
    public Long countByUser(User user) {
        return videoRepository.countByUserAndStatusTrue(user);
    }
    
    // Validate video trước khi lưu
    public boolean validateVideo(Video video) {
        if (video.getTitle() == null || video.getTitle().trim().isEmpty()) {
            return false;
        }
        
        if (video.getCategory() == null) {
            return false;
        }
        
        if (video.getUser() == null) {
            return false;
        }
        
        return true;
    }
    
    // Lấy thống kê video
    public VideoStatistics getVideoStatistics() {
        long totalVideos = videoRepository.count();
        long activeVideos = videoRepository.findByStatusTrue().size();
        
        return new VideoStatistics(totalVideos, activeVideos);
    }
    
    // Inner class cho thống kê
    public static class VideoStatistics {
        private long totalVideos;
        private long activeVideos;
        
        public VideoStatistics(long totalVideos, long activeVideos) {
            this.totalVideos = totalVideos;
            this.activeVideos = activeVideos;
        }
        
        public long getTotalVideos() {
            return totalVideos;
        }
        
        public void setTotalVideos(long totalVideos) {
            this.totalVideos = totalVideos;
        }
        
        public long getActiveVideos() {
            return activeVideos;
        }
        
        public void setActiveVideos(long activeVideos) {
            this.activeVideos = activeVideos;
        }
    }
}
