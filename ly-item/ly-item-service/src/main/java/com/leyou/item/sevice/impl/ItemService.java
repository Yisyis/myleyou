package com.leyou.item.sevice.impl;

import com.leyou.item.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemService {

    public Item saveItem(Item item){
        // 商品新增
        int id = new Random().nextInt(100);
        item.setId(id);
        item.setName("鼠标");
        item.setPrice(99);
        return item;
    }
}
