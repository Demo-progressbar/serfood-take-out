package com.seafood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.Result;
import com.seafood.dto.SetmealDto;
import com.seafood.entity.Category;

import com.seafood.entity.Setmeal;
import com.seafood.entity.SetmealDish;
import com.seafood.service.CategoryService;

import com.seafood.service.SetmealDishService;
import com.seafood.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 保存套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save (@RequestBody SetmealDto setmealDto){

        log.info("新增套餐 {}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return Result.success("新增套餐成功");
    }

    /**
     * 套餐分類查詢
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page (Long page,Long pageSize , String name) {

        //創建分頁物件查詢數據
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        //創建Page<SetmealDto>
        Page<SetmealDto> dtoPage = new Page<>();

        //設置查詢條件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null ,Setmeal::getName,name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        
        //查詢Setmeal
        setmealService.page(pageInfo, queryWrapper);


        //把pageInfo數據拷貝到dtoPage
        BeanUtils.copyProperties(pageInfo ,dtoPage ,"records");

        //將List<Setmeal>集合屬據封裝進List<SetmealDto>
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtoList =  records.stream().map((item)->{

            //透過item獲取CategoryId查詢套餐分類表
            Category category = categoryService.getById(item.getCategoryId());

            //創建SetmealDto
            SetmealDto setmealDto = new SetmealDto();
            //封裝數據
            BeanUtils.copyProperties(item,setmealDto);

            setmealDto.setCategoryName(category.getName());

            return setmealDto;

        }).collect(Collectors.toList());

        //把setmealDtoList封裝進dtoPage
        dtoPage.setRecords(setmealDtoList);

        return Result.success(dtoPage);
    }

    /**
     * 刪除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete (@RequestParam List<Long> ids){

        log.info("要刪除的套餐ID {}" ,ids);

        setmealService.removeWithDish(ids);

        return Result.success("套餐刪除成功");
    }

    /**
     * 狀態修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status (@PathVariable Integer status , @RequestParam List<Long> ids){

        log.info("狀態修改參數 status= {} , ids = {}" ,status ,ids);

        setmealService.updateStatus(status,ids);

        return Result.success("售賣狀態更改成功");
    }


    /**
     * 修改數據回寫
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> get (@PathVariable Long id) {

        log.info("修改數據回寫參數 {}",id);
        SetmealDto setmealDto = setmealService.getByIdWithSetmealDish(id);

        return Result.success(setmealDto);
    }


    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update (@RequestBody SetmealDto setmealDto){
        log.info("修改套餐參數 = {}",setmealDto);

        setmealService.updateWithSetmealDish(setmealDto);

        return Result.success("套餐修改成功");
    }

    /**
     * 展示套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list (Setmeal setmeal){

        //用菜品分類id查詢對應的套餐種類
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId ,setmeal.getCategoryId());
        //查詢啟售狀態的套餐
        queryWrapper.eq(Setmeal::getStatus ,1);
        //按照更新時間降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return Result.success(list);
    }

    /**
     * 前端套餐詳情展示
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public Result<List<SetmealDish>> setmealDetails (@PathVariable Long id){

        log.info("前端套餐詳情展示 {}",id);

        List<SetmealDish> setmealDishes = setmealService.setmealDetails(id);



        return Result.success(setmealDishes);
    }



}
