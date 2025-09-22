package com.login.baitap5.controller;

import com.login.baitap5.entity.Video;
import com.login.baitap5.entity.Category;
import com.login.baitap5.entity.User;
import com.login.baitap5.service.VideoService;
import com.login.baitap5.service.CategoryService;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/videos")
public class AdminVideoController {
    
    @Autowired
    private VideoService videoService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("")
    public String listVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean status,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Video> videos;
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (categoryId != null) {
                Optional<Category> categoryOpt = categoryService.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    videos = videoService.searchVideos(keyword, categoryOpt.get(), pageable);
                } else {
                    videos = videoService.searchVideos(keyword, pageable);
                }
            } else if (userId != null) {
                Optional<User> userOpt = userService.findById(userId);
                if (userOpt.isPresent()) {
                    videos = videoService.searchVideos(keyword, userOpt.get(), pageable);
                } else {
                    videos = videoService.searchVideos(keyword, pageable);
                }
            } else if (status != null) {
                videos = videoService.searchVideos(keyword, status, pageable);
            } else {
                videos = videoService.searchVideos(keyword, pageable);
            }
        } else {
            videos = videoService.findAll(pageable);
        }
        
        List<Category> categories = categoryService.findActiveCategories();
        List<User> users = userService.findActiveUsers();
        
        model.addAttribute("videos", videos);
        model.addAttribute("categories", categories);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "admin/videos/list";
    }
    
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("video", new Video());
        model.addAttribute("categories", categoryService.findActiveCategories());
        model.addAttribute("users", userService.findActiveUsers());
        return "admin/videos/form";
    }
    
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute Video video, 
                        BindingResult result, 
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findActiveCategories());
            model.addAttribute("users", userService.findActiveUsers());
            return "admin/videos/form";
        }
        
        try {
            videoService.save(video);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo video thành công!");
            return "redirect:/admin/videos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo video!");
            return "redirect:/admin/videos/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Video> videoOpt = videoService.findById(id);
        if (videoOpt.isPresent()) {
            model.addAttribute("video", videoOpt.get());
            model.addAttribute("categories", categoryService.findActiveCategories());
            model.addAttribute("users", userService.findActiveUsers());
            return "admin/videos/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy video!");
            return "redirect:/admin/videos";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, 
                        @Valid @ModelAttribute Video video, 
                        BindingResult result, 
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findActiveCategories());
            model.addAttribute("users", userService.findActiveUsers());
            return "admin/videos/form";
        }
        
        try {
            video.setId(id);
            videoService.update(video);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật video thành công!");
            return "redirect:/admin/videos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật video!");
            return "redirect:/admin/videos/edit/" + id;
        }
    }
    
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Video> videoOpt = videoService.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            model.addAttribute("video", video);
            
            // Lấy videos liên quan
            if (video.getCategory() != null) {
                Page<Video> relatedVideos = videoService.getRelatedVideos(
                    video.getCategory(), video.getId(), PageRequest.of(0, 5));
                model.addAttribute("relatedVideos", relatedVideos.getContent());
            }
            
            return "admin/videos/view";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy video!");
            return "redirect:/admin/videos";
        }
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa video thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa video!");
        }
        return "redirect:/admin/videos";
    }
    
    @PostMapping("/soft-delete/{id}")
    public String softDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.softDelete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa video thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi vô hiệu hóa video!");
        }
        return "redirect:/admin/videos";
    }
    
    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.restore(id);
            redirectAttributes.addFlashAttribute("successMessage", "Khôi phục video thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi khôi phục video!");
        }
        return "redirect:/admin/videos";
    }
    
    @PostMapping("/increment-views/{id}")
    public String incrementViews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.incrementViews(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tăng lượt xem thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra!");
        }
        return "redirect:/admin/videos/view/" + id;
    }
    
    @PostMapping("/increment-likes/{id}")
    public String incrementLikes(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.incrementLikes(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tăng lượt like thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra!");
        }
        return "redirect:/admin/videos/view/" + id;
    }
    
    @GetMapping("/popular")
    public String popularVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoService.getMostPopularVideos(pageable);
        
        model.addAttribute("videos", videos);
        model.addAttribute("title", "Video Phổ Biến Nhất");
        
        return "admin/videos/popular";
    }
    
    @GetMapping("/newest")
    public String newestVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoService.getNewestVideos(pageable);
        
        model.addAttribute("videos", videos);
        model.addAttribute("title", "Video Mới Nhất");
        
        return "admin/videos/newest";
    }
}
