package com.seafood.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.Result;
import com.seafood.entity.Category;
import com.seafood.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;


    /**
     * 新增分類
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save (@RequestBody Category category){

        log.info("Category {}",category);

        categoryService.save(category);

        return Result.success("新增分類成功");
    }

    /**
     * 分頁查詢
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page (Long page,Long pageSize){

        //創建Page物件
        Page<Category> pageInfo = new Page<>(page,pageSize);

        //設定排序條件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);
        //分頁查詢
        categoryService.page(pageInfo,queryWrapper);


        return Result.success(pageInfo);
    }

    /**
     * 根據ID刪除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteById (Long ids){

        log.info("刪除接收到的Id {}",ids);

        //categoryService.removeById(ids);

        //調用判斷是否有關連資料刪除
        categoryService.remove(ids);

        return Result.success("分類信息刪除成功");
    }


    /**
     * 根據id修改
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update (@RequestBody Category category) {

        log.info("修改分類訊息 {}",category);

        categoryService.updateById(category);

        return Result.success("分類訊息修改成功");
    }
    @GetMapping("/list")
    public Result<List<Category>> list (Category category){

        //判斷參數是否為空, 若不為空則查詢
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() !=null,Category::getType,category.getType());

        //設定排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //查詢list
        List<Category> list = categoryService.list(queryWrapper);

        return Result.success(list);
    }




}
