package com.leyou.item.sevice.impl;

import com.leyou.item.mapper.TransAreaMapper;
import com.leyou.item.pojo.Area;
import com.leyou.item.sevice.TransAreaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author YS
 * @date 2020/9/18
 */
public class TransAreaServiceImpl implements TransAreaService {


    public static int i = 0;

    @Autowired
    private static TransAreaMapper transAreaMapper;

    public List<Area> queryAllArea() {
        List<Area> areas = transAreaMapper.queryAllArea();



        return areas;
    }

}
