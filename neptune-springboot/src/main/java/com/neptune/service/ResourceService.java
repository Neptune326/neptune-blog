package com.neptune.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neptune.entity.Resource;
import com.neptune.vo.admin.ResourceVo;
import com.neptune.vo.admin.TreeVo;

import java.util.List;
import java.util.Map;

/**
 * 权限资源表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface ResourceService extends IService<Resource> {

    List<?> getList(boolean select);

    Page<ResourceVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search);

    boolean saveUpdate(Resource t);

    List<TreeVo> getTree();
}
