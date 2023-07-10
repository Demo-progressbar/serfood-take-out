package com.seafood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seafood.entity.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加購物車
     * @param shoppingCart
     * @return
     */
    ShoppingCart add (ShoppingCart shoppingCart);


    /**
     * 購物車商品-1
     * @param shoppingCart
     * @return
     */
    ShoppingCart sub (ShoppingCart shoppingCart);


    /**
     * 查看購物車清單
     * @return
     */
    List<ShoppingCart> cartList ();

    /**
     * 清空購物車
     */
    void clean ();
}
