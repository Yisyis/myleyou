package com.leyou.item.sevice.impl;

import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.Specification;
import com.leyou.item.sevice.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;

    @Override
    public Specification getById(Long id) {
        return specificationMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insertSpecification(Specification specification) {
        specificationMapper.insert(specification);
    }

    @Override
    public void deleteSpecification(Long id) {
        specificationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateSpecification(Specification specification) {
        specificationMapper.updateByPrimaryKeySelective(specification);
    }
}
