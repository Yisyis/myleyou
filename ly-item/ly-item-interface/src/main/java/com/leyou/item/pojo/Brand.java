package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_brand") // 用来命名当前实体类对应的数据库表的名字
public class Brand {

    /**
     * -AUTO主键由程序控制, 是默认选项 ,不设置就是这个
     * -IDENTITY 主键由数据库生成, 采用数据库自增长, Oracle不支持这种方式
     * -SEQUENCE 通过数据库的序列产生主键, MYSQL不支持
     * -Table 提供特定的数据库产生主键, 该方式更有利于数据库的移植
     */
    @Id // 主键字段
    @GeneratedValue(strategy = GenerationType.IDENTITY) //为实体生成唯一标识的主键
    private Long id;
    private String name; // 品牌名称
    private String image; // 品牌图片
    private Character letter;

}
