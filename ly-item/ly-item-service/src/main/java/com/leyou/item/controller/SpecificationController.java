package com.leyou.item.controller;

import com.leyou.item.pojo.Specification;
import com.leyou.item.sevice.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 查询商品分类对应的规格参数模板
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> getSpecificationByCategoryId(@PathVariable("id") Long id) {
        Specification spec = this.specificationService.getById(id);
        if (spec == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spec.getSpecifications());
    }

    /**
     * 新增一个规格参数模板
     * @param specification
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> insertSpecification(Specification specification) {
        this.specificationService.insertSpecification(specification);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格参数模板
     * @param specification
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateSpecification(Specification specification) {
        this.specificationService.updateSpecification(specification);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * 删除一个规格参数模板
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecification(@PathVariable("id") Long id) {
        this.specificationService.deleteSpecification(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
