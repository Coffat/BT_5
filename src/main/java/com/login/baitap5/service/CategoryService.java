package com.login.baitap5.service;

import com.login.baitap5.entity.Category;
import com.login.baitap5.entity.Video;
import com.login.baitap5.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private AuditService auditService;
    
    // Lấy tất cả categories
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    // Lấy categories với phân trang
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    // Tìm category theo ID
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    // Tìm category theo tên
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    // Lấy tất cả categories đang hoạt động
    public List<Category> findActiveCategories() {
        return categoryRepository.findByStatusTrue();
    }
    
    // Tìm kiếm categories theo keyword
    public Page<Category> searchCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByKeyword(keyword.trim(), pageable);
    }
    
    // Tìm kiếm categories theo keyword và status
    public Page<Category> searchCategories(String keyword, Boolean status, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByKeywordAndStatus(keyword.trim(), status, pageable);
    }
    
    // Lưu category
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    // Cập nhật category
    public Category update(Category category) {
        return categoryRepository.save(category);
    }
    
    // Xóa category theo ID (cần kiểm tra an toàn trước)
    public void deleteById(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            
            // Kiểm tra xem có videos nào đang liên kết không
            if (category.getVideos() != null && !category.getVideos().isEmpty()) {
                auditService.logSecurityViolation("UNSAFE_DELETE_ATTEMPT", 
                    "Attempted to delete Category ID:" + id + " with " + category.getVideos().size() + " videos", 
                    "SYSTEM", "localhost");
                throw new RuntimeException("Không thể xóa danh mục này vì vẫn còn " + 
                    category.getVideos().size() + " video(s) liên quan. Vui lòng xóa hoặc chuyển các video này trước.");
            }
            
            auditService.logDelete("Category", id, "ADMIN");
        }
        
        categoryRepository.deleteById(id);
    }
    
    // Xóa mềm category (chỉ thay đổi status) và đồng bộ với videos
    public void softDelete(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setStatus(false);
            
            int affectedVideos = 0;
            // Đồng bộ vô hiệu hóa tất cả videos thuộc category này
            if (category.getVideos() != null) {
                for (com.login.baitap5.entity.Video video : category.getVideos()) {
                    if (video.getStatus()) { // Chỉ vô hiệu hóa những video đang hoạt động
                        video.setStatus(false);
                        affectedVideos++;
                    }
                }
            }
            
            auditService.logSoftDelete("Category", id, "ADMIN");
            if (affectedVideos > 0) {
                auditService.logCascadeOperation("SOFT_DELETE", "Category", id, "Video", affectedVideos, "ADMIN");
            }
            
            categoryRepository.save(category);
        }
    }
    
    // Khôi phục category (lưu ý: videos sẽ không tự động được khôi phục để tránh khôi phục nhầm)
    public void restore(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setStatus(true);
            
            // Lưu ý: Không tự động khôi phục videos vì có thể admin đã cố ý vô hiệu hóa từng video riêng lẻ
            // Admin cần khôi phục videos thủ công nếu muốn
            
            auditService.logRestore("Category", id, "ADMIN");
            categoryRepository.save(category);
        }
    }
    
    // Kiểm tra tên category đã tồn tại chưa
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
    
    // Kiểm tra tên category đã tồn tại chưa (trừ category hiện tại)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }
    
    // Đếm tổng số categories
    public long count() {
        return categoryRepository.count();
    }
    
    // Lấy categories với số lượng video
    public List<Object[]> getCategoriesWithVideoCount() {
        return categoryRepository.findCategoriesWithVideoCount();
    }
    
    // Validate category trước khi lưu
    public boolean validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra tên đã tồn tại chưa
        if (category.getId() == null) {
            return !existsByName(category.getName());
        } else {
            return !existsByNameAndIdNot(category.getName(), category.getId());
        }
    }
}
