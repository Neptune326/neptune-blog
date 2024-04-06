package com.neptune.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.annotation.BackLog;
import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import com.neptune.constant.MinioConstant;
import com.neptune.dto.PhotoAlbumDto;
import com.neptune.entity.PhotoAlbum;
import com.neptune.entity.ResponseResult;
import com.neptune.service.PhotoAlbumService;
import com.neptune.strategy.UploadStrategy;
import com.neptune.vo.admin.PhotoAlbumVo;
import com.neptune.vo.admin.UploadVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/admin/photoAlbum")
public class PhotoAlbumController {

    @Autowired
    private PhotoAlbumService photoAlbumService;
    @Autowired
    private UploadStrategy uploadStrategy;

    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) PhotoAlbumDto dto) {
        PhotoAlbum t = BeanUtil.copyProperties(dto, PhotoAlbum.class);
        boolean flag = photoAlbumService.save(t);
        return ResponseResult.success(flag);
    }

    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) PhotoAlbumDto dto) {
        PhotoAlbum t = photoAlbumService.getById(dto.getId());
        BeanUtils.copyProperties(dto, t);
        boolean flag = photoAlbumService.updateById(t);
        return ResponseResult.success(flag);
    }

    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = photoAlbumService.removeById(id);
        return ResponseResult.success(flag);
    }

    @GetMapping("info")
    public ResponseResult<PhotoAlbumVo> getInfo(@RequestParam Long id) {
        PhotoAlbum t = photoAlbumService.getById(id);
        PhotoAlbumVo vo = BeanUtil.copyProperties(t, PhotoAlbumVo.class);
        return ResponseResult.success(vo);
    }

    @GetMapping("list")
    public ResponseResult<List<PhotoAlbumVo>> list() {
        List<PhotoAlbumVo> list = photoAlbumService.listAs(QueryWrapper.create(), PhotoAlbumVo.class);
        return ResponseResult.success(list);
    }

    @BackLog(module = "相册", type = "上传封面")
    @PostMapping("/upload")
    public ResponseResult<?> upload(MultipartFile file) {
        String url = uploadStrategy.uploadAndGetUrl(file, MinioConstant.BUCKET_PHOTO);
        UploadVo vo = UploadVo.builder().url(url).build();
        return ResponseResult.success(vo);
    }

}
