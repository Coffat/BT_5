package com.login.baitap5.service;

import com.login.baitap5.entity.Category;
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
    
    // Xóa category theo ID
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
    
    // Xóa mềm category (chỉ thay đổi status)
    public void softDelete(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setStatus(false);
            categoryRepository.save(category);
        }
    }
    
    // Khôi phục category
    public void restore(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setStatus(true);
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
