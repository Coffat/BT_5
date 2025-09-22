package com.login.baitap5.validation;

import com.login.baitap5.entity.User;
import com.login.baitap5.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidUserValidator implements ConstraintValidator<ValidUser, User> {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void initialize(ValidUser constraintAnnotation) {
        // Không cần khởi tạo gì đặc biệt
    }
    
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user == null || user.getId() == null) {
            return false;
        }
        
        // Kiểm tra user có tồn tại và đang hoạt động không
        return userService.findById(user.getId())
                .map(u -> u.getStatus() != null && u.getStatus())
                .orElse(false);
    }
}
