package com.leyou.item.pojo;

import lombok.Data;
import lombok.Generated;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_spu")
public class Spu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long brandId;
    private Long cid1; // 1级类目
    private Long cid2; // 2级类目
    private Long cid3; // 3级类目
    private String title; // 标题
    private String subTitle; // 子标题
    private Boolean saleable; // 是否上架
    private Boolean valid; // 是否有效，逻辑删除用
    private Date createTime; // 创建时间
    private Date lastUpdateTime; // 最后修改时间

    public Spu() {
    }

    public Spu(Long brandId, Long cid1, Long cid2, Long cid3, String title, String subTitle, Boolean saleable, Boolean valid, Date createTime, Date lastUpdateTime) {
        this.brandId = brandId;
        this.cid1 = cid1;
        this.cid2 = cid2;
        this.cid3 = cid3;
        this.title = title;
        this.subTitle = subTitle;
        this.saleable = saleable;
        this.valid = valid;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
    }

}
