package com.neptune.controller.admin;

import com.mybatisflex.core.paginate.Page;
import com.neptune.annotation.BackLog;
import com.neptune.constant.MinioConstant;
import com.neptune.entity.Photo;
import com.neptune.entity.ResponseResult;
import com.neptune.service.PhotoService;
import com.neptune.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;


@RestController
@RequestMapping("/admin/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;
    @Autowired
    private UploadStrategy uploadStrategy;

    @BackLog(module = "图片", type = "上传图片")
    @PostMapping("/upload")
    public ResponseResult<?> upload(MultipartFile file, @RequestParam Long albumId) {
        String url = uploadStrategy.uploadAndGetUrl(file, MinioConstant.BUCKET_PHOTO);
        Photo t = Photo.builder()
                .name(file.getName())
                .url(url)
                .albumId(albumId)
                .build();
        boolean flag = photoService.save(t);
        return ResponseResult.success(flag);
    }

    /**
     * 添加照片表。
     *
     * @param photo 照片表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Photo photo) {
        return photoService.save(photo);
    }

    /**
     * 根据主键删除照片表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return photoService.removeById(id);
    }

    /**
     * 根据主键更新照片表。
     *
     * @param photo 照片表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Photo photo) {
        return photoService.updateById(photo);
    }

    /**
     * 查询所有照片表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Photo> list() {
        return photoService.list();
    }

    /**
     * 根据照片表主键获取详细信息。
     *
     * @param id 照片表主键
     * @return 照片表详情
     */
    @GetMapping("getInfo/{id}")
    public Photo getInfo(@PathVariable Serializable id) {
        return photoService.getById(id);
    }

    /**
     * 分页查询照片表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Photo> page(Page<Photo> page) {
        return photoService.page(page);
    }

}
