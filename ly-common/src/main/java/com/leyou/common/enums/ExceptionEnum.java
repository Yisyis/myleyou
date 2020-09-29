package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    PRICE_CANNOT_BE_NULL(404, "价格不能为空！"),
    BRAND_NOT_FOUND(404, "品牌不存在"),
    BRAND_SAVE_ERROR(500, "新增品牌不存在"),
    FILE_UPLOAD_ERROR(500, "文件上传失败"),
    INVALID_FILE_TYPE(500, "文件类型不匹配"),

    CATEGORY_NOT_FOUND(500, "分类不匹配"),
            ;
    private Integer code;
    private String msg;

}
