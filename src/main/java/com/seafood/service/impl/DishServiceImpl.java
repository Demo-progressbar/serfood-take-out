package com.seafood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.common.CustomException;
import com.seafood.dto.DishDto;
import com.seafood.entity.Dish;
import com.seafood.entity.DishFlavor;
import com.seafood.entity.Setmeal;
import com.seafood.entity.SetmealDish;
import com.seafood.mapper.DishMapper;
import com.seafood.service.DishFlavorService;
import com.seafood.service.DishService;
import com.seafood.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper , Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 保存菜品+口味
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //保存進菜品表
        this.save(dishDto);

        //獲取dish的Id
        Long dishId = dishDto.getId();

        //設置Flavor表的dishId
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map( (item)->{

            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存Flavor表
        dishFlavorService.saveBatch(flavors);


    }

    /**
     * 根據Id查詢菜品, 需查兩張表dish flavor
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //創建DishDto
        DishDto dishDto = new DishDto();

        //根據id查詢dish
        Dish dish = this.getById(id);

        //把dish中數據拷貝進DishDto
        BeanUtils.copyProperties(dish,dishDto);

        //根據Id查詢Flavor表
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorQueryWrapper);

        //封裝flavorList到DishDto
        dishDto.setFlavors(flavorList);


        return dishDto;
    }


    /**
     * 修改菜品, 需查兩張表dish flavor
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {

        //保存進dish表
        this.updateById(dishDto);

        //獲取dishId
        Long dishId = dishDto.getId();

        //刪除原flavor裡的dishDto.dishId的關聯資料
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);

        dishFlavorService.remove(queryWrapper);

        //設置flavors裡的dishId
        List<DishFlavor> flavorList = dishDto.getFlavors();

        flavorList = flavorList.stream().map( (item)->{

            item.setDishId(dishId);

            return item;

        }).collect(Collectors.toList());

        //保存進口味表
        dishFlavorService.saveBatch(flavorList);


    }

    /**
     * 刪除菜品, 需刪兩張表dish flavor
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithFlavor(List<Long> ids) {

        //判斷當前菜品是否停售
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getStatus ,1);
        dishLambdaQueryWrapper.in(Dish::getId,ids);

        int count1 = this.count(dishLambdaQueryWrapper);

        if (count1 > 0 ){
            throw new CustomException("當前菜品為售賣狀態, 不可刪除");
        }

        //判斷該菜品是否有關聯套餐
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId ,ids);

        int count2 = setmealDishService.count(setmealDishLambdaQueryWrapper);

        if(count2 > 0) {
            throw new CustomException("當前菜品有關連套餐資料, 不可刪除");
        }


        //根據dishId刪除flavor表關聯數據
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);

        dishFlavorService.remove(queryWrapper);

        //刪除菜品表數據
        this.removeByIds(ids);

    }


    /**
     * 根據ids修改停售&啟售
     * @param status
     * @param ids
     */
    @Override
    public void updateStatusByIds(Integer status, List<Long> ids) {

        //根據傳入的ids查詢dish集合
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Dish::getId ,ids);

        List<Dish> dishList = this.list(queryWrapper);

        //遍歷dish集合修改status
        dishList = dishList.stream().map( (item)->{

            item.setStatus(status);

            return item;

        }).collect(Collectors.toList());
        //修改status
        this.updateBatchById(dishList);
    }




}
