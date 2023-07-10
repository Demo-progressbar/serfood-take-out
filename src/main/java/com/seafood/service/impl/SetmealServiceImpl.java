package com.seafood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.common.CustomException;
import com.seafood.dto.SetmealDto;
import com.seafood.entity.Dish;
import com.seafood.entity.Setmeal;
import com.seafood.entity.SetmealDish;
import com.seafood.mapper.SetmealMapper;
import com.seafood.service.DishService;
import com.seafood.service.SetmealDishService;
import com.seafood.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;


    @Autowired
    private DishService dishService;

    /**
     * 保存套餐表 (關聯套餐_菜品表)
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存進套餐表
        this.save(setmealDto);

        //設置套餐_菜品表的套餐Id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item)->{

            item.setSetmealId(setmealDto.getId());

            return item;

        }).collect(Collectors.toList());

        //保存進套餐_菜品表

        setmealDishService.saveBatch(setmealDishes);


    }
    /**
     * 刪除套餐 (多表)
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {

        //判斷要刪除的套餐是否為售賣狀態
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //查詢是否為售賣狀態
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);

        int count = this.count(setmealLambdaQueryWrapper);

        //如有查出紀錄則拋異常
        if(count > 0) {
            throw new CustomException("套餐為啟售狀態, 不能刪除");
        }

        //刪除套餐菜品關聯表數據
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        //刪除套餐表數據
        this.removeByIds(ids);

    }
    /**
     * 啟售 & 停售套餐
     * @param status
     * @param ids
     */

    @Override
    public void updateStatus(Integer status, List<Long> ids) {

        //設定查詢條件查出欲修改的套餐物件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);
        //查詢套餐
        List<Setmeal> setmeals = this.list(queryWrapper);

        //修改查出的套餐list, 設定為停售(status = 0)
        setmeals = setmeals.stream().map((item)->{

            item.setStatus(status);

            return item;

        }).collect(Collectors.toList());

        //修改套餐
        this.updateBatchById(setmeals);


    }

    /**
     * 修改回寫
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithSetmealDish(Long id) {

        //根據Id查詢setmeal表
        Setmeal setmeal = this.getById(id);

        //創建SetmealDto物件
        SetmealDto setmealDto = new SetmealDto();

        //拷貝setmeal物件資料進SetmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);


        //查詢Setmeal關聯菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        //把查詢到的數據封裝進SetmealDto
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /**
     * 修改套餐(多表操作)
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateWithSetmealDish(SetmealDto setmealDto) {

        //修改Setmeal表數據
        this.updateById(setmealDto);

        //刪除原SetmealDish數據
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId ,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //重新添加新數據
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item)->{

            item.setSetmealId(setmealDto.getId());

            return item;

        }).collect(Collectors.toList());

        //更新SetmealDish
        setmealDishService.saveBatch(setmealDishes);


    }

    /**
     * 套餐詳情展示
     * @param id
     * @return
     */
    @Override
    public List<SetmealDish> setmealDetails(Long id) {

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        //設置圖片進setmealDishes集合
        setmealDishes = setmealDishes.stream().map( (item)->{

            Dish dish = dishService.getById(item.getDishId());
            //把圖片存放進setmealDishe
            item.setImage(dish.getImage());

            return item;

        }).collect(Collectors.toList());

        return setmealDishes;
    }
}
