package com.leyou.item.sevice.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.sevice.CategoryService;
import com.leyou.item.sevice.GoodsService;
import com.leyou.parameter.pojo.SpuQueryByPageParameter;
import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.esf.SPUserNotice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    /**
     * 分页查询
     *
     * @param spuQueryByPageParameter
     * @return
     */
    @Override
    public PageResult<SpuBo> getSpuByPageAndSort(SpuQueryByPageParameter spuQueryByPageParameter) {
        // 1.查询spu，分页查询，最多查询100条
        PageHelper.startPage(spuQueryByPageParameter.getPage(), Math.min(spuQueryByPageParameter.getRows(), 100));

        // 2.创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 3.条件过滤
        // 3.1 是否过滤上下架
        if (spuQueryByPageParameter.getSaleable() != null) {
            criteria.orEqualTo("saleable", spuQueryByPageParameter.getSaleable());
        }
        // 3.2 是否过滤模糊查询
        if (StringUtils.isNotEmpty(spuQueryByPageParameter.getKey())) {
            criteria.orEqualTo("key", spuQueryByPageParameter.getKey());
        }
        // 3.3 是否排序
        if (StringUtils.isNotEmpty(spuQueryByPageParameter.getSortBy())) {
            criteria.orEqualTo("sortBy", spuQueryByPageParameter.getSortBy());
        }
        Page<Spu> pageInfo = (Page<Spu>) this.spuMapper.selectByExample(example);

        // 将spu变为spubo
        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            // 1.属性拷贝
            BeanUtils.copyProperties(spu, spuBo);
            // 2.查询spu的商品分类名称，各级分类
            List<String> nameList = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 3.拼接名字，并存入
            spuBo.setCname(StringUtils.join(nameList, "/"));
            // 4.查询品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), list);
    }

    /**
     * 保存商品
     *
     * @param spu
     */
    @Override
    public void saveGoods(SpuBo spu) {
        // 保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);

        // 保存spu详情
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        this.spuDetailMapper.insert(spuDetail);

        // 保存sku和库存信息
        saveSkuAndStock(spu.getSkus(), spu.getId());

        // 发送消息给mq
    }

    private void saveSkuAndStock(List<Sku> skus, Long id) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(id);
            // 默认不参加任何促销
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);

            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateGoods(SpuBo spuBo) {
        /**
         * 更新策略：
         *      1.判断tb_spu_detail中的spec_template字段新旧是否一致
         *      2.如果一致说明修改的只是库存、价格和是否启用，那么就使用update
         *      3.如果不一致，说明修改了特有属性，那么需要把原来的sku全部删除，然后添加新的sku
         */
        // 更新spu
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setLastUpdateTime(new Date());
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 更新spu详情
        SpuDetail spuDetail = spuBo.getSpuDetail();
        String oldTemplate = this.spuDetailMapper.selectByPrimaryKey(spuBo.getId()).getSpecTemplate();
        if (spuDetail.getSpecTemplate().equals(oldTemplate)) {
            // 相等， sku update
            // 更新sku和库存信息
            updateSkuAndStock(spuBo.getSkus(), spuBo.getId(), true);
        } else {
            // 不等，sku insert
            // 更新sku和库存信息
            updateSkuAndStock(spuBo.getSkus(), spuBo.getId(), false);
        }
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.updateByPrimaryKeySelective(spuDetail);

    }

    private void updateSkuAndStock(List<Sku> skus, Long id, boolean tag) {
        // 通过tag判断是insert还是update
        // 获取当前数据库中 spu_id = id 的sku信息
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", id);
        // oldList中保存数据库中 spuId = id 的全部sku
        List<Sku> oldList = this.skuMapper.selectByExample(example);
        if (tag) {
            /*
            判断更新时是否有新的sku被添加：
                如果对已有数据更新的话，则此时oldList中的数据和skus中的ownSpec是相同的，
                否则则需要新增
             */
            int count = 0;
            for (Sku sku : skus) {
                if (!sku.getEnable()) {
                    continue;
                }
                for (Sku old : oldList) {
                    if (sku.getOwnSpec().equals(old.getOwnSpec())) {
                        // 更新
                        List<Sku> list = this.skuMapper.select(old);
                        // Sku new = this.skuMapper.selectByPrimaryKey(old);
                        if (sku.getPrice() == null) {
                            sku.setPrice(0L);
                        }
                        if (sku.getStock() == null) {
                            sku.setStock(0L);
                        }
                        sku.setId(list.get(0).getId());
                        sku.setCreateTime(list.get(0).getCreateTime());
                        sku.setSpuId(list.get(0).getSpuId());
                        sku.setLastUpdateTime(new Date());
                        this.skuMapper.updateByPrimaryKey(sku);
                        // 更新库存信息
                        Stock stock = new Stock();
                        stock.setSkuId(sku.getId());
                        stock.setStock(sku.getStock());
                        this.stockMapper.updateByPrimaryKeySelective(stock);
                        // 从oldList中将更新完的数据删除
                        oldList.remove(old);
                        break;
                    } else {
                        // 新增
                        count++;
                    }
                }
                if (count == oldList.size() && count != 0) {
                    // 当只有一个sku时，更新完因为从oldList中将其移除，所以长度为0，需要加不为0的条件
                    List<Sku> addSku = new ArrayList<>();
                    addSku.add(sku);
                    saveSkuAndStock(addSku, id);
                    count = 0;
                } else {
                    count = 0;
                }
            }
            // 处理脏数据
            if (oldList.size() != 0) {
                for (Sku sku : oldList) {
                    this.skuMapper.deleteByPrimaryKey(sku.getId());
                    Example e = new Example(Stock.class);
                    e.createCriteria().andEqualTo("skuId", sku.getId());
                    this.stockMapper.deleteByExample(e);
                }
            }
        } else {
            List<Long> ids = oldList.stream().map(Sku::getId).collect(Collectors.toList());
            // 删除以前的库存
            Example e = new Example(Stock.class);
            e.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(e);
            // 删除以前的sku
            Example e1 = new Example(Sku.class);
            e1.createCriteria().andEqualTo("spuId", id);
            this.skuMapper.deleteByExample(e1);
            // 新增sku和库存
            saveSkuAndStock(skus, id);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void goodsSoldOut(Long id) {
        // 上架或下架spu中的商品
        Spu oldSpu = this.spuMapper.selectByPrimaryKey(id);
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", id);
        List<Sku> skuList = this.skuMapper.selectByExample(example);
        if (oldSpu.getSaleable()) {
            // 下架
            oldSpu.setSaleable(false);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            // 下架sku中的具体商品
            for (Sku sku : skuList) {
                sku.setEnable(false);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            }
        } else {
            // 上架
            oldSpu.setSaleable(true);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            // 上架sku中的具体商品
            for (Sku sku : skuList) {
                sku.setEnable(true);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            }
        }

        // 发送消息到mq
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGoods(Long id) {
        // 删除spu表中的数据
        this.spuMapper.deleteByPrimaryKey(id);

        // 删除spu_detail中的数据
        Example example = new Example(SpuDetail.class);
        example.createCriteria().andEqualTo("spuId", id);
        this.spuDetailMapper.deleteByExample(example);

        List<Sku> skuList = this.skuMapper.selectByExample(example);
        for (Sku sku : skuList) {
            // 删除Sku中的数据
            this.skuMapper.deleteByPrimaryKey(sku.getId());
            // 删除stock中的数据
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        }

        // 发送消息到mq

    }

    @Override
    public SpuDetail getSpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }
}
