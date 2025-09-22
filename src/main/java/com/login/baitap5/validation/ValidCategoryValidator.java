package com.login.baitap5.validation;

import com.login.baitap5.entity.Category;
import com.login.baitap5.service.CategoryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidCategoryValidator implements ConstraintValidator<ValidCategory, Category> {
    
    @Autowired
    private CategoryService categoryService;
    
    @Override
    public void initialize(ValidCategory constraintAnnotation) {
        // Không cần khởi tạo gì đặc biệt
    }
    
    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        if (category == null || category.getId() == null) {
            return false;
        }
        
        // Kiểm tra category có tồn tại và đang hoạt động không
        return categoryService.findById(category.getId())
                .map(cat -> cat.getStatus() != null && cat.getStatus())
                .orElse(false);
    }
}
