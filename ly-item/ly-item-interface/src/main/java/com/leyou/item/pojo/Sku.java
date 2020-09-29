package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_sku")
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY：主键由数据库自动生成（主要是自动增长型）
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private String ownSpec; // 商品特殊规格的键值对
    private String indexes; // 商品特殊规格的下标
    private Boolean enable; // 是否有效，逻辑删除用
    private Date createTime; // 创建时间
    private Date lastUpdateTime; // 最后修改时间
    @Transient // 表示该属性并非一个到数据库表的字段映射，ORM框架将忽略该属性
    private Long stock;

    @Override
    public String toString() {
        return "Sku{" +
                "id=" + id +
                ", spuId=" + spuId +
                ", title='" + title + '\'' +
                ", images='" + images + '\'' +
                ", price=" + price +
                ", ownSpec='" + ownSpec + '\'' +
                ", indexes='" + indexes + '\'' +
                ", enable=" + enable +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", stock=" + stock +
                '}';
    }
}
