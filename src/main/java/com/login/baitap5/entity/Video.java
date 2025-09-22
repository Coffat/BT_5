package com.login.baitap5.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.login.baitap5.validation.ValidCategory;
import com.login.baitap5.validation.ValidUser;

import java.time.LocalDateTime;

@Entity
@Table(name = "Video")
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tiêu đề video không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    @Column(name = "title", nullable = false)
    private String title;
    
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "description")
    private String description;
    
    @Size(max = 500, message = "URL video không được vượt quá 500 ký tự")
    @Column(name = "video_url")
    private String videoUrl;
    
    @Size(max = 500, message = "URL thumbnail không được vượt quá 500 ký tự")
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "duration")
    private Integer duration; // thời lượng tính bằng giây
    
    @Column(name = "views")
    private Long views = 0L;
    
    @Column(name = "likes")
    private Long likes = 0L;
    
    @Column(name = "status")
    private Boolean status = true;
    
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @NotNull(message = "Danh mục không được để trống")
    @ValidCategory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @NotNull(message = "Người dùng không được để trống")
    @ValidUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public Video() {}
    
    public Video(String title, String description, String videoUrl, Category category, User user) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.category = category;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Long getViews() {
        return views;
    }
    
    public void setViews(Long views) {
        this.views = views;
    }
    
    public Long getLikes() {
        return likes;
    }
    
    public void setLikes(Long likes) {
        this.likes = likes;
    }
    
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Helper method để format thời lượng
    public String getFormattedDuration() {
        if (duration == null) return "00:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", duration=" + duration +
                ", views=" + views +
                ", likes=" + likes +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
