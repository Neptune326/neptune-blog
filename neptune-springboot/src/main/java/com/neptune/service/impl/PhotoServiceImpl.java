package com.neptune.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.Photo;
import com.neptune.mapper.PhotoMapper;
import com.neptune.service.PhotoService;
import org.springframework.stereotype.Service;

/**
 * 照片表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

}
