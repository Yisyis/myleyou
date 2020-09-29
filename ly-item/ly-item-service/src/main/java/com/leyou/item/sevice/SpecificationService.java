package com.leyou.item.sevice;

import com.leyou.item.pojo.Specification;

public interface SpecificationService {

    Specification getById(Long id);

    void insertSpecification(Specification specification);

    void deleteSpecification(Long id);

    void updateSpecification(Specification specification);

}
