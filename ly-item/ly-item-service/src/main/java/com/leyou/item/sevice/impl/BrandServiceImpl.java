package com.leyou.item.sevice.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.sevice.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(
            Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 开始分页 PageHelper会利用mybatis的拦截器对后面要执行的sql进行拦截，自动地在后面拼上 limit 的语句
        PageHelper.startPage(page, rows);
        // 过滤 根据Brand.class 找到表@Table(name = "tb_brand")
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            // 过滤条件 WHERE name Like "%X%" OR letter == 'x'
            //          ORDER BY id DESC
            example.createCriteria()   // 创建标准 条件
                    .andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause); // 设置排序子句
        }
        // 查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        // 解析分页结果
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), brands);
    }

    @Transactional(rollbackFor = Exception.class) // 事务
    public void saveBrand(Brand brand, List<Long> cids) {
        // 新增品牌信息 INSERT INTO tb_brand ( id,name,image,letter ) VALUES( ?,?,?,? )
        int count = this.brandMapper.insertSelective(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        // 新增品牌和分类中间表
        for (Long cid : cids) {
            // INSERT INTO tb_category_brand (category_id, brand_id) VALUES (?, ?)
            count = this.brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(Brand brand, List<Long> cids) {
        // 删除原来的数据
        deleteByBrandIdInCategoryBrand(brand.getId());
        // 修改品牌信息
        this.brandMapper.updateByPrimaryKeySelective(brand);
        // 维护品牌和分类中间表
        for (Long cid: cids) {
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
        }
    }


    public void deleteByBrandIdInCategoryBrand(Long bid) {
        this.brandMapper.deleteByBrandIdInCategoryBrand(bid);
    }

    @Override
    public void deleteBrand(Long id) {
        // 删除品牌信息
        this.brandMapper.deleteByPrimaryKey(id);
        // 维护中间表
        this.brandMapper.deleteByBrandIdInCategoryBrand(id);
    }

    @Override
    public List<Brand> queryBrandByCategoryId(Long cid) {
        return this.brandMapper.queryBrandByCategoryId(cid);
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        return this.brandMapper.selectByIdList(ids);
    }
}
