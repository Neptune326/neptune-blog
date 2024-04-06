package com.neptune.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.PhotoAlbum;
import com.neptune.mapper.PhotoAlbumMapper;
import com.neptune.service.PhotoAlbumService;
import org.springframework.stereotype.Service;

/**
 * 相册表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbum> implements PhotoAlbumService {

}
