package com.login.baitap5.controller;

import com.login.baitap5.service.CategoryService;
import com.login.baitap5.service.UserService;
import com.login.baitap5.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VideoService videoService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Thống kê tổng quan
        long totalCategories = categoryService.count();
        long totalUsers = userService.count();
        long totalVideos = videoService.count();
        
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalVideos", totalVideos);
        
        return "admin/dashboard";
    }
    
    @GetMapping("")
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }
}
