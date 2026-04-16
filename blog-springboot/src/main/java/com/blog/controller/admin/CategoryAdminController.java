package com.blog.controller.admin;

import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.dto.CategoryDTO;
import com.blog.service.CategoryService;
import com.blog.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.list());
    }

    @OperationLog(module = "分类管理", action = "创建分类")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CategoryDTO dto) {
        log.info("创建分类：{}", dto.getName());
        categoryService.create(dto);
        return Result.success();
    }

    @OperationLog(module = "分类管理", action = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        log.info("更新分类 ID={}：{}", id, dto.getName());
        categoryService.update(id, dto);
        return Result.success();
    }

    @OperationLog(module = "分类管理", action = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除分类 ID={}", id);
        categoryService.delete(id);
        return Result.success();
    }
}
