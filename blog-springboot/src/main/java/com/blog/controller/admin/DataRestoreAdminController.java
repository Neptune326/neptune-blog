package com.blog.controller.admin;

import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.entity.Category;
import com.blog.entity.FriendLink;
import com.blog.entity.SysConfig;
import com.blog.entity.Tag;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.FriendLinkMapper;
import com.blog.mapper.SysConfigMapper;
import com.blog.mapper.TagMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 数据恢复接口（JSON）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/data")
@Validated
@RequiredArgsConstructor
public class DataRestoreAdminController {
    private final ObjectMapper objectMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final FriendLinkMapper friendLinkMapper;
    private final SysConfigMapper sysConfigMapper;

    @OperationLog(module = "系统管理", action = "数据恢复")
    @PostMapping("/restore")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Integer>> restore(@RequestParam("file") MultipartFile file,
                                                @RequestParam(value = "mode", defaultValue = "upsert") String mode) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error(com.blog.common.result.ResultCode.BAD_REQUEST, "请选择备份文件");
        }
        Map<String, Object> backup = objectMapper.readValue(file.getInputStream(), new TypeReference<Map<String, Object>>() {});
        Map<String, Integer> result = new HashMap<>();
        result.put("categories", restoreCategories(backup.get("categories"), mode));
        result.put("tags", restoreTags(backup.get("tags"), mode));
        result.put("friendLinks", restoreFriendLinks(backup.get("friendLinks"), mode));
        result.put("sysConfig", restoreSysConfig(backup.get("sysConfig"), mode));
        log.info("[数据恢复] 执行完成，mode: {}，结果: {}", mode, result);
        return Result.success(result);
    }

    private int restoreCategories(Object raw, String mode) {
        List<Category> list = parseList(raw, Category.class);
        return upsertList(list, mode, "categories");
    }

    private int restoreTags(Object raw, String mode) {
        List<Tag> list = parseList(raw, Tag.class);
        return upsertList(list, mode, "tags");
    }

    private int restoreFriendLinks(Object raw, String mode) {
        List<FriendLink> list = parseList(raw, FriendLink.class);
        return upsertList(list, mode, "friendLinks");
    }

    private int restoreSysConfig(Object raw, String mode) {
        List<SysConfig> list;
        if (raw instanceof Map<?, ?> m && !m.containsKey("records")) {
            list = new java.util.ArrayList<>();
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                SysConfig item = new SysConfig();
                item.setConfigKey(String.valueOf(entry.getKey()));
                item.setConfigValue(entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
                list.add(item);
            }
        } else {
            list = parseList(raw, SysConfig.class);
        }
        int count = 0;
        for (SysConfig item : list) {
            if (item.getConfigKey() == null || item.getConfigKey().isBlank()) {
                continue;
            }
            SysConfig existing = sysConfigMapper.selectOne(
                    new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, item.getConfigKey())
            );
            if ("insert".equalsIgnoreCase(mode) && existing != null) {
                continue;
            }
            if (existing == null) {
                sysConfigMapper.insert(item);
            } else {
                item.setId(existing.getId());
                sysConfigMapper.updateById(item);
            }
            count++;
        }
        return count;
    }

    private <T> List<T> parseList(Object raw, Class<T> clazz) {
        if (raw instanceof List<?>) {
            return objectMapper.convertValue(raw, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        }
        if (!(raw instanceof Map<?, ?> map)) {
            return List.of();
        }
        Object data = map.get("records");
        if (data == null) {
            data = map.get("list");
        }
        if (data == null) {
            data = map.get("items");
        }
        if (data == null && map.get("data") instanceof List<?>) {
            data = map.get("data");
        }
        if (data == null && raw instanceof List<?>) {
            data = raw;
        }
        if (data == null) {
            return List.of();
        }
        return objectMapper.convertValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    private int upsertList(List<?> list, String mode, String tableName) {
        int count = 0;
        for (Object item : list) {
            if (item instanceof Category category) {
                Category existing = category.getId() == null ? null : categoryMapper.selectById(category.getId());
                if ("insert".equalsIgnoreCase(mode) && existing != null) {
                    continue;
                }
                if (existing == null) {
                    categoryMapper.insert(category);
                } else {
                    categoryMapper.updateById(category);
                }
                count++;
            } else if (item instanceof Tag tag) {
                Tag existing = tag.getId() == null ? null : tagMapper.selectById(tag.getId());
                if ("insert".equalsIgnoreCase(mode) && existing != null) {
                    continue;
                }
                if (existing == null) {
                    tagMapper.insert(tag);
                } else {
                    tagMapper.updateById(tag);
                }
                count++;
            } else if (item instanceof FriendLink friendLink) {
                FriendLink existing = friendLink.getId() == null ? null : friendLinkMapper.selectById(friendLink.getId());
                if ("insert".equalsIgnoreCase(mode) && existing != null) {
                    continue;
                }
                if (existing == null) {
                    friendLinkMapper.insert(friendLink);
                } else {
                    friendLinkMapper.updateById(friendLink);
                }
                count++;
            }
        }
        log.info("[数据恢复] {} 处理数量: {}", tableName, count);
        return count;
    }
}
