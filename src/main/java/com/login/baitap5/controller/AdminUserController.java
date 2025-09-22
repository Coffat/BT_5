package com.login.baitap5.controller;

import com.login.baitap5.entity.User;
import com.login.baitap5.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) User.Role role,
            @RequestParam(required = false) Boolean status,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (role != null) {
                users = userService.searchUsers(keyword, role, pageable);
            } else if (status != null) {
                users = userService.searchUsers(keyword, status, pageable);
            } else {
                users = userService.searchUsers(keyword, pageable);
            }
        } else {
            users = userService.findAll(pageable);
        }
        
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("status", status);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "admin/users/list";
    }
    
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "admin/users/form";
    }
    
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute User user, 
                        BindingResult result, 
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }
        
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Tên đăng nhập đã tồn tại");
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email đã tồn tại");
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }
        
        try {
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo người dùng thành công!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo người dùng!");
            return "redirect:/admin/users/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Xóa password để không hiển thị trong form
            user.setPassword("");
            model.addAttribute("user", user);
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, 
                        @Valid @ModelAttribute User user, 
                        BindingResult result, 
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }
        
        if (userService.existsByUsernameAndIdNot(user.getUsername(), id)) {
            result.rejectValue("username", "error.user", "Tên đăng nhập đã tồn tại");
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }
        
        if (userService.existsByEmailAndIdNot(user.getEmail(), id)) {
            result.rejectValue("email", "error.user", "Email đã tồn tại");
            model.addAttribute("roles", User.Role.values());
            return "admin/users/form";
        }
        
        try {
            user.setId(id);
            userService.update(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật người dùng!");
            return "redirect:/admin/users/edit/" + id;
        }
    }
    
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "admin/users/view";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa người dùng!");
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/soft-delete/{id}")
    public String softDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.softDelete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi vô hiệu hóa người dùng!");
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.restore(id);
            redirectAttributes.addFlashAttribute("successMessage", "Khôi phục người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi khôi phục người dùng!");
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/reset-password/{id}")
    public String resetPasswordForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "admin/users/reset-password";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
    }
    
    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id, 
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "redirect:/admin/users/reset-password/" + id;
        }
        
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "redirect:/admin/users/reset-password/" + id;
        }
        
        try {
            userService.resetPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Reset mật khẩu thành công!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi reset mật khẩu!");
            return "redirect:/admin/users/reset-password/" + id;
        }
    }
}
