//package com.leyou.item.controller;
//
//import com.leyou.common.vo.PageResult;
//import com.leyou.item.bo.SpuBo;
//import com.leyou.item.pojo.SpuDetail;
//import com.leyou.item.sevice.GoodsService;
//import com.leyou.parameter.pojo.SpuQueryByPageParameter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/goods")
//public class GoodsController {
//
//    @Autowired
//    private GoodsService goodsService;
//
//    /**
//     * 分页查询
//     *
//     * @param page
//     * @param rows
//     * @param sortBy
//     * @param desc
//     * @param key
//     * @param saleable
//     * @return
//     */
//    @GetMapping("/spu/page")
//    public ResponseEntity<PageResult<SpuBo>> getSpuByPage(
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
//            @RequestParam(value = "sortBy", required = false) String sortBy,
//            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
//            @RequestParam(value = "key", required = false) String key,
//            @RequestParam(value = "saleable", defaultValue = "true") Boolean saleable
//    ) {
//        // 分页查询spu信息
//        SpuQueryByPageParameter spuQueryByPageParameter = new SpuQueryByPageParameter(page, rows, sortBy, desc, key, saleable);
//        PageResult<SpuBo> result = this.goodsService.getSpuByPageAndSort(spuQueryByPageParameter);
//        if (result == null || result.getItems().size() == 0) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 保存商品
//     *
//     * @param spu
//     * @return
//     */
//    @PostMapping
//    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spu) {
//        this.goodsService.saveGoods(spu);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    /**
//     * 修改商品
//     *
//     * @param spu
//     * @return
//     */
//    @PutMapping
//    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spu) {
//        this.goodsService.updateGoods(spu);
//        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
//    }
//
//    /**
//     * 商品上下架
//     *
//     * @param ids
//     * @return
//     */
//    @PutMapping("/spu/out/{id}")
//    public ResponseEntity<Void> goodsSoldOut(@PathVariable("id") String ids) {
//        String seqarator = "-";
//        if (ids.contains(seqarator)) {
//            String[] goodsId = ids.split(seqarator);
//            for (String id : goodsId) {
//                this.goodsService.goodsSoldOut(Long.parseLong(id));
//            }
//        } else {
//            this.goodsService.goodsSoldOut(Long.parseLong(ids));
//        }
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    /**
//     * 删除商品
//     *
//     * @param ids
//     * @return
//     */
//    @DeleteMapping("/spu/{id}")
//    public ResponseEntity<Void> deleteGoods(@PathVariable("id") String ids) {
//        String seqarator = "-";
//        if (ids.contains(seqarator)) {
//            String[] goodsId = ids.split(seqarator);
//            for (String id : goodsId) {
//                this.goodsService.deleteGoods(Long.parseLong(id));
//            }
//        } else {
//            this.goodsService.deleteGoods(Long.parseLong(ids));
//        }
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    /**
//     * 查询商品详情
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/spu/detail/{id}")
//    public ResponseEntity<SpuDetail> getSpuDetailById(@PathVariable("id") Long id) {
//        SpuDetail spuDetail = this.goodsService.getSpuDetailById(id);
//        if (spuDetail == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return ResponseEntity.ok(spuDetail);
//    }
//}
