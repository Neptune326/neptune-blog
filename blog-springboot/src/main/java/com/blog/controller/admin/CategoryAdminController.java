package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.dto.CategoryDTO;
import com.blog.service.CategoryService;
import com.blog.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理 Controller
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    /**
     * 查询所有分类列表
     */
    @GetMapping
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.list());
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CategoryDTO dto) {
        categoryService.create(dto);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        categoryService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
