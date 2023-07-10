package com.seafood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.common.BaseContext;
import com.seafood.entity.ShoppingCart;
import com.seafood.mapper.ShoppingCartMapper;
import com.seafood.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper , ShoppingCart> implements ShoppingCartService {

    @Autowired
    ShoppingCartService shoppingCartService;



    /**
     * 添加購物車
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {


        //設置當前登入者id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //判斷當次加入購物車的是採品還是套餐
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if (dishId != null){
            //說明當前新增的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
            //說明當前新增的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //判斷當次添加的菜品或是套餐是否為第一次添加
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one == null) {
            //第一次添加, 設置數量
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }else {
            //並非第一次添加, 數量+1
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        }


        return one;
    }

    /**
     * 購物車商品-1
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {

        //獲取當前登入userId
        Long currentId = BaseContext.getCurrentId();

        //判斷當前操作是菜品還是套餐
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if(one.getNumber() == 1){
            one.setNumber(null);
            shoppingCartService.removeById(one);
            return one;

        }else {
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
        }

        return one;
    }

    /**
     * 查看購物車清單
     * @return
     */
    @Override
    public List<ShoppingCart> cartList() {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId , BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        return shoppingCarts;
    }

    /**
     * 清空購物車
     */
    @Override
    public void clean() {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

    }
}
