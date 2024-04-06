package com.neptune.mapper;

import com.mybatisflex.core.BaseMapper;
import com.neptune.entity.Menu;
import com.neptune.vo.admin.MenuVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 菜单表 映射层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<MenuVo> listAll(@Param("param") Map<String, String> search);
}
