package com.leyou.item.controller;


import com.leyou.item.pojo.Category;
import com.leyou.item.sevice.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //@RestController=@ResponseBody ＋ @Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据parentId查询类目
     *
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam("pid") Long pid) {
        //如果pid的值为-1，那么需要获取数据库中最后一条数据
        if (pid == -1) {
            List<Category> last = this.categoryService.queryLast();
            return ResponseEntity.ok(last);
        } else {
            List<Category> list = this.categoryService.queryCategoryByPid(pid);
            if (list == null) {
                // 返回404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            // 找到返回200
            return ResponseEntity.ok(list);
        }
    }

    /**
     * 用于修改商品时，回显信息
     *
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@RequestParam("bid") Long bid) {
        List<Category> list = this.categoryService.queryByBrandId(bid);
        if (list == null || list.size() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 新增
     *
     * @param category
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveCategory(Category category) {
        this.categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新
     *
     * @param category
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCategory(Category category) {
        this.categoryService.updateCategory(category);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * 删除
     */
    @DeleteMapping("cid/{cid}")
    public ResponseEntity<Void> deleteCategory(@RequestParam("cid") Long id) {
        this.categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
