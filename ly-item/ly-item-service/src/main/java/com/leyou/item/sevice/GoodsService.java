package com.leyou.item.sevice;

import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.parameter.pojo.SpuQueryByPageParameter;

public interface GoodsService {

    PageResult<SpuBo> getSpuByPageAndSort(SpuQueryByPageParameter spuQueryByPageParameter);

    void saveGoods(SpuBo spu);

    void updateGoods(SpuBo spu);

    void goodsSoldOut(Long id);

    void deleteGoods(Long id);

    SpuDetail getSpuDetailById(Long id);
}
