package com.leyou.item.controller;

import com.leyou.item.pojo.Area;
import com.leyou.item.sevice.TransAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YS
 * @date 2020/9/18
 */
@RestController
@RequestMapping("/area")
public class TransAreaController {

    @Autowired
    private TransAreaService transAreaService;

    @GetMapping("/page")
    public ResponseEntity<List<Area>> queryBrandByPage() {
        List<Area> areas = transAreaService.queryAllArea();

        return ResponseEntity.ok(areas);
    }


}
