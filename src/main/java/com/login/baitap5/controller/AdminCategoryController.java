package com.login.baitap5.controller;

import com.login.baitap5.entity.Category;
import com.login.baitap5.service.CategoryService;
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
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("")
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean status,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Category> categories;
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status != null) {
                categories = categoryService.searchCategories(keyword, status, pageable);
            } else {
                categories = categoryService.searchCategories(keyword, pageable);
            }
        } else {
            categories = categoryService.findAll(pageable);
        }
        
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "admin/categories/list";
    }
    
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }
    
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute Category category, 
                        BindingResult result, 
                        RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "admin/categories/form";
        }
        
        if (categoryService.existsByName(category.getName())) {
            result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
            return "admin/categories/form";
        }
        
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo danh mục!");
            return "redirect:/admin/categories/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOpt = categoryService.findById(id);
        if (categoryOpt.isPresent()) {
            model.addAttribute("category", categoryOpt.get());
            return "admin/categories/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục!");
            return "redirect:/admin/categories";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, 
                        @Valid @ModelAttribute Category category, 
                        BindingResult result, 
                        RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "admin/categories/form";
        }
        
        if (categoryService.existsByNameAndIdNot(category.getName(), id)) {
            result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
            return "admin/categories/form";
        }
        
        try {
            category.setId(id);
            categoryService.update(category);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật danh mục!");
            return "redirect:/admin/categories/edit/" + id;
        }
    }
    
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOpt = categoryService.findById(id);
        if (categoryOpt.isPresent()) {
            model.addAttribute("category", categoryOpt.get());
            return "admin/categories/view";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục!");
            return "redirect:/admin/categories";
        }
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa danh mục!");
        }
        return "redirect:/admin/categories";
    }
    
    @PostMapping("/soft-delete/{id}")
    public String softDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.softDelete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi vô hiệu hóa danh mục!");
        }
        return "redirect:/admin/categories";
    }
    
    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.restore(id);
            redirectAttributes.addFlashAttribute("successMessage", "Khôi phục danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi khôi phục danh mục!");
        }
        return "redirect:/admin/categories";
    }
}
