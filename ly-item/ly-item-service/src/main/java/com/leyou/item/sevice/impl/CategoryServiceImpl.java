package com.leyou.item.sevice.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.sevice.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据parentId查询子类目
     *
     * @param pid
     * @return
     */
    @Override
    public List<Category> queryCategoryByPid(Long pid) {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", pid);
        List<Category> list = this.categoryMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<Category> queryLast() {
        return this.categoryMapper.selectLast();
    }

    @Override
    public List<Category> queryByBrandId(Long bid) {
        Category category = new Category();
        category.setId(bid);
        return this.categoryMapper.select(category);
    }

    @Override
    public List<String> queryNameByIds(List<Long> aslist) {
        List<String> names = new ArrayList<>();
        if (aslist != null && aslist.size() != 0) {
            for (Long id : aslist) {
                names.add(this.categoryMapper.queryNameById(id));
            }
        }
        return names;
        // 使用通用mapper接口中的SelectByIdListMapper接口查询
        // return this.categoryMapper.selectByIdList(aslist).stream().map(Category::getName).collect(Collectors.toList());
    }

    public Integer saveCategory(Category category) {
        /**
         *  将本节点插入到数据库中
         *  将category的父节点isParent为true
         */
        //1.首先置id为null
        category.setId(null);
        //2.保存
        this.categoryMapper.insert(category);
        //3.修改父节点
        Category parent = new Category();
        parent.setId(category.getParentId());
        parent.setIsParent(true);
        return this.categoryMapper.updateByPrimaryKeySelective(parent);
    }

    public Integer updateCategory(Category category) {
        return this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    public void deleteCategory(Long id) {
        /**
         *  先根据id查询要删除的对象，然后进行判断
         *  如果是父节点，删除所有附带子节点，然后维护中间表
         *  如果是子节点，只删除自己，然后判断父节点孩子的个数，如果孩子不为0，则不做修改；
         *      如果孩子个数为0，则修改父节点isParent的值为false，最后维护中间表
         */
        Category category = this.categoryMapper.selectByPrimaryKey(id);
        if (category.getIsParent()) {
            //1.查找所有叶子节点
            List<Category> list = new ArrayList<>();
            queryAllLeafNode(category, list);
            //2.查找所有子节点
            List<Category> list2 = new ArrayList<>();
            queryAllNode(category, list2);
            //3.删除tb_category中的数据，使用list2
            for (Category c : list2) {
                this.categoryMapper.delete(c);
            }
            //4.维护(删除)中间表
            for (Category c : list) {
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(c.getId());
            }
        } else {
            //1.查询此节点的父节点的孩子个数  ==》 查询还有几个兄弟节点
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("parentId", category.getParentId());
            List<Category> list = this.categoryMapper.selectByExample(example);
            if (list.size() != 1) {
                // 有兄弟，直接删除自己
                this.categoryMapper.deleteByPrimaryKey(category.getId());
                // 维护中间表
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            } else {
                // 没有兄弟了
                this.categoryMapper.deleteByPrimaryKey(category.getId());
                Category parent = new Category();
                parent.setId(category.getParentId());
                parent.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(parent);
                // 维护中间表
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }
        }
    }

    /**
     * 查询本节点下所包含的所有叶子节点，用于维护tb_category_brand中间表
     *
     * @param category
     * @param leafNode
     */
    private void queryAllLeafNode(Category category, List<Category> leafNode) {
        if (!category.getIsParent()) {
            leafNode.add(category);
        }
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", category.getId());
        List<Category> list = this.categoryMapper.selectByExample(example);
        for (Category category1 : list) {
            queryAllLeafNode(category1, leafNode);
        }
    }

    /**
     * 查询本节点下所有子节点(包括父节点)
     *
     * @param category
     * @param node
     */
    private void queryAllNode(Category category, List<Category> node) {
        node.add(category);
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", category.getId());
        List<Category> list = this.categoryMapper.selectByExample(example);
        for (Category category1 : list) {
            queryAllNode(category1, node);
        }
    }

}
