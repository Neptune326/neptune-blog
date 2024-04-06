package com.neptune.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neptune.entity.Menu;
import com.neptune.vo.admin.MenuVo;
import com.neptune.vo.admin.SelectVo;
import com.neptune.vo.admin.TreeVo;

import java.util.List;
import java.util.Map;

/**
 * 菜单表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface MenuService extends IService<Menu> {

    Page<MenuVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search);

    List<Menu> getList();

    List<MenuVo> getListVo();

    List<SelectVo> getSelectVo();

    List<TreeVo> getTree();

    boolean saveUpdate(Menu t);

    List<MenuVo> listAll(Map<String, String> search);

    boolean updateVisibleStatus(Long id, Integer isHidden);
}
