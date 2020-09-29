package com.leyou.item.sevice;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

public interface BrandService {

    PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    void updateBrand(Brand brand, List<Long> cids);

    void deleteByBrandIdInCategoryBrand(Long bid);

    void deleteBrand(Long id);

    List<Brand> queryBrandByCategoryId(Long cid);

    List<Brand> queryBrandByIds(List<Long> ids);
}
