package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.dto.TagDTO;
import com.blog.service.TagService;
import com.blog.vo.TagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台标签管理 Controller
 */
@RestController
@RequestMapping("/api/admin/tags")
@Validated
@RequiredArgsConstructor
public class TagAdminController {

    private final TagService tagService;

    /**
     * 查询所有标签列表
     */
    @GetMapping
    public Result<List<TagVO>> list() {
        return Result.success(tagService.list());
    }

    /**
     * 创建标签
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody TagDTO dto) {
        tagService.create(dto);
        return Result.success();
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TagDTO dto) {
        tagService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }
}
