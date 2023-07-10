package com.seafood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.Result;
import com.seafood.dto.DishDto;
import com.seafood.entity.Category;
import com.seafood.entity.Dish;
import com.seafood.service.CategoryService;
import com.seafood.service.DishFlavorService;
import com.seafood.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){

        log.info("dishDto = {}",dishDto);

        dishService.saveWithFlavor(dishDto);

        return Result.success("新增菜品成功");
    }

    /**
     * 菜品分頁查詢
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page (Long page, Long pageSize , String name){

        //創建Page物件
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        //創建DishDto的Page物件
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //設定查詢條件
        queryWrapper.like(name != null,Dish::getName,name);
        //設定排序條件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);

        //把pageInfo物件資料拷貝給dishDtoPage, 並排除records屬性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //處理pageInfo的records屬性, 把數據封裝到dishDtoPage的records
        List<Dish> records = pageInfo.getRecords();

        //把處理好的records數據封裝到dishDtoList
        List<DishDto> dishDtoList = records.stream().map( (item)->{
            //創建DishDto物件
            DishDto dishDto = new DishDto();

            //把遍歷出來的dish數據拷貝到dishDto
            BeanUtils.copyProperties(item,dishDto);
            //封裝categoryName屬性
            Category category = categoryService.getById(item.getCategoryId());

            if (category != null){
            dishDto.setCategoryName(category.getName());
            }

            return dishDto;

        }).collect(Collectors.toList());

        //把處理好的dishDtoList封裝進dishDtoPage
        dishDtoPage.setRecords(dishDtoList);

        return Result.success(dishDtoPage);
    }

    /**
     * 修改回寫數據
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return Result.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update (@RequestBody DishDto dishDto){

        log.info("修改dishDto = {}",dishDto);

        dishService.updateWithFlavor(dishDto);

        return Result.success("菜品修改成功");
    }

    /**
     * 刪除 & 批量刪除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam("ids") List<Long> ids){

        log.info("接受到的dishId為 {}",ids);

        dishService.deleteWithFlavor(ids);

        return Result.success("數據刪除成功");
    }


    /**
     * 停售 & 批量停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status ,@RequestParam("ids") List<Long> ids) {

        log.info("接受到的status = {} , dishID = {}",status,ids);

        dishService.updateStatusByIds(status,ids);

        return Result.success("售賣狀態修改成功");
    }

    /**
     * 查詢菜品列表 (套餐)
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public Result<List<Dish>> list (Dish dish){
//        //創建查詢條件物件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//
//        //設定查詢條件
//        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId ,dish.getCategoryId());
//
//        //查詢狀態為1(啟售狀態菜品)
//        queryWrapper.eq(Dish::getStatus , 1);
//
//        //設定排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        //查詢
//        List<Dish> dishList = dishService.list(queryWrapper);
//
//        return Result.success(dishList);
//    }

    /**
     * 查詢菜品列表 (套餐)
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list (Dish dish){
        //創建查詢條件物件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //設定查詢條件
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId ,dish.getCategoryId());

        //查詢狀態為1(啟售狀態菜品)
        queryWrapper.eq(Dish::getStatus , 1);

        //設定排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        //查詢
        List<Dish> dishList = dishService.list(queryWrapper);

        //把Dish封裝成DishDto
        List<DishDto> dishDtoList = dishList.stream().map((item)->{

            //用dishId查詢Flavor表,結果封裝進 DishDto
            DishDto dishDto = dishService.getByIdWithFlavor(item.getId());

            return dishDto;

        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }


}
