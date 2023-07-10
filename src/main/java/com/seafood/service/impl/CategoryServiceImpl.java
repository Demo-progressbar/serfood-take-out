package com.seafood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.common.CustomException;
import com.seafood.entity.Category;
import com.seafood.entity.Dish;
import com.seafood.entity.Setmeal;
import com.seafood.mapper.CategoryMapper;
import com.seafood.service.CategoryService;
import com.seafood.service.DishService;
import com.seafood.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper , Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     *根據ID刪除分類, 刪除前需先判斷
     * @param id
     */
    @Override
    public void remove(Long id) {

        //設置Dish表查詢條件
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);

        //設置Setmeal表查詢條件
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealQueryWrapper);

        //判斷當前要刪除的分類在Dish表內是否有關連數據, 有則拋異常

        if(count1 > 0){
           //有關聯資料, 拋異常
            throw new CustomException("當前分類已關聯菜品, 不能刪除");
        }

        //判斷當前要刪除的分類在Setmeal表內是否有關連數據, 有則拋異常

        if(count2 > 0){
            //有關聯資料, 拋異常
            throw new CustomException("當前分類已關聯套餐, 不能刪除");
        }

        //無關連數據執行刪除
        super.removeById(id);

    }
}
