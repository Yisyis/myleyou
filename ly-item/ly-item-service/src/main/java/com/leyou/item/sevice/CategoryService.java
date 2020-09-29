package com.leyou.item.sevice;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {

    List<Category> queryCategoryByPid(Long pid);

    List<Category> queryLast();

    List<Category> queryByBrandId(Long bid);

    List<String> queryNameByIds(List<Long> aslist);

    Integer saveCategory(Category category);

    Integer updateCategory(Category category);

    void deleteCategory(Long id);

}


