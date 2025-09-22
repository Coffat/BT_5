package com.login.baitap5.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log hoạt động tạo mới
     */
    public void logCreate(String entityType, Long entityId, String adminUser) {
        String message = String.format("[CREATE] %s ID:%d được tạo bởi %s lúc %s", 
            entityType, entityId, adminUser, LocalDateTime.now().format(formatter));
        logger.info(message);
    }
    
    /**
     * Log hoạt động cập nhật
     */
    public void logUpdate(String entityType, Long entityId, String adminUser) {
        String message = String.format("[UPDATE] %s ID:%d được cập nhật bởi %s lúc %s", 
            entityType, entityId, adminUser, LocalDateTime.now().format(formatter));
        logger.info(message);
    }
    
    /**
     * Log hoạt động xóa cứng
     */
    public void logDelete(String entityType, Long entityId, String adminUser) {
        String message = String.format("[DELETE] %s ID:%d được xóa vĩnh viễn bởi %s lúc %s", 
            entityType, entityId, adminUser, LocalDateTime.now().format(formatter));
        logger.warn(message);
    }
    
    /**
     * Log hoạt động xóa mềm
     */
    public void logSoftDelete(String entityType, Long entityId, String adminUser) {
        String message = String.format("[SOFT_DELETE] %s ID:%d được vô hiệu hóa bởi %s lúc %s", 
            entityType, entityId, adminUser, LocalDateTime.now().format(formatter));
        logger.info(message);
    }
    
    /**
     * Log hoạt động khôi phục
     */
    public void logRestore(String entityType, Long entityId, String adminUser) {
        String message = String.format("[RESTORE] %s ID:%d được khôi phục bởi %s lúc %s", 
            entityType, entityId, adminUser, LocalDateTime.now().format(formatter));
        logger.info(message);
    }
    
    /**
     * Log lỗi bảo mật
     */
    public void logSecurityViolation(String violationType, String details, String userAgent, String ipAddress) {
        String message = String.format("[SECURITY_VIOLATION] %s - %s từ IP:%s UserAgent:%s lúc %s", 
            violationType, details, ipAddress, userAgent, LocalDateTime.now().format(formatter));
        logger.error(message);
    }
    
    /**
     * Log lỗi validation
     */
    public void logValidationError(String entityType, String fieldName, String errorMessage, String adminUser) {
        String message = String.format("[VALIDATION_ERROR] %s.%s: %s - User:%s lúc %s", 
            entityType, fieldName, errorMessage, adminUser, LocalDateTime.now().format(formatter));
        logger.warn(message);
    }
    
    /**
     * Log cascade operations (xóa category ảnh hưởng đến videos)
     */
    public void logCascadeOperation(String operation, String parentEntity, Long parentId, 
                                  String childEntity, int affectedCount, String adminUser) {
        String message = String.format("[CASCADE_%s] %s ID:%d -> %d %s bị ảnh hưởng bởi %s lúc %s", 
            operation, parentEntity, parentId, affectedCount, childEntity, adminUser, 
            LocalDateTime.now().format(formatter));
        logger.warn(message);
    }
}
