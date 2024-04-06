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
@RequestMapping("/admin/attachment")
public class AttachmentController {

    @Autowired
    private UploadStrategy uploadStrategy;

    @BackLog(module = "附件", type = "上传")
    @PostMapping("/upload")
    public ResponseResult<String> upload(MultipartFile file ) {
        String url = uploadStrategy.uploadAndGetUrl(file, MinioConstant.BUCKET_DEFAULT);
        return ResponseResult.success("上传成功", url);
    }

}
