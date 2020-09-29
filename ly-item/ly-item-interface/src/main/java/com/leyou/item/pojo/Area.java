package com.leyou.item.pojo;

import lombok.Data;

/**
 * @author YS
 * @date 2020/9/18
 */
@Data
public class Area {

    private int id;
    private int parentId;
    private String name;
    private String iconSkin;
    private String treeType;
    private String thirdId;
    private String parentThirdId;

}
