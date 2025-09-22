package com.login.baitap5.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Xử lý lỗi vi phạm ràng buộc cơ sở dữ liệu
     * Thay vì hiển thị lỗi HTTP 500, sẽ hiển thị thông báo thân thiện
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, 
                                             HttpServletRequest request,
                                             RedirectAttributes redirectAttributes) {
        
        String errorMessage = "Có lỗi xảy ra với dữ liệu. ";
        
        // Phân tích lỗi cụ thể
        String exceptionMessage = ex.getMessage().toLowerCase();
        
        if (exceptionMessage.contains("category_id")) {
            errorMessage += "Vui lòng chọn danh mục hợp lệ.";
        } else if (exceptionMessage.contains("user_id")) {
            errorMessage += "Vui lòng chọn người dùng hợp lệ.";
        } else if (exceptionMessage.contains("duplicate") || exceptionMessage.contains("unique")) {
            errorMessage += "Dữ liệu đã tồn tại trong hệ thống.";
        } else if (exceptionMessage.contains("null") || exceptionMessage.contains("not-null")) {
            errorMessage += "Vui lòng điền đầy đủ thông tin bắt buộc.";
        } else {
            errorMessage += "Vui lòng kiểm tra lại thông tin đã nhập.";
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        
        // Redirect về trang trước đó hoặc trang chính
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        
        return "redirect:/admin/dashboard";
    }
    
    /**
     * Xử lý các lỗi runtime khác
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, 
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        
        return "redirect:/admin/dashboard";
    }
    
    /**
     * Xử lý lỗi chung
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, 
                                       HttpServletRequest request,
                                       Model model) {
        
        model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        model.addAttribute("errorDetails", ex.getMessage());
        
        return "error/500"; // Tạo trang error tùy chỉnh
    }
}
