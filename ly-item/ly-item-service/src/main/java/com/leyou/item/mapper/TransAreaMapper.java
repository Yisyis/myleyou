package com.leyou.item.mapper;

import com.leyou.item.pojo.Area;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author YS
 * @date 2020/9/18
 */
public interface TransAreaMapper {

    @Select("SELECT * FROM t_resource_area_newbak")
    List<Area> queryAllArea();


}
