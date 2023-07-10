package com.seafood.controller;


import com.seafood.common.Result;
import com.seafood.entity.ShoppingCart;
import com.seafood.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;


    /**
     * 添加購物車
     * @return
     */
    @PostMapping ("/add")
    public Result<ShoppingCart> add (@RequestBody ShoppingCart shoppingCart){

        log.info("添加購物車參數 {}",shoppingCart);

        ShoppingCart cart = shoppingCartService.add(shoppingCart);


        return Result.success(cart);
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list (){
        log.info("查看購物車");

        List<ShoppingCart> shoppingCarts = shoppingCartService.cartList();

        return Result.success(shoppingCarts);
    }

    /**
     * 清空購物車
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean (){

        shoppingCartService.clean();

        return Result.success("購物車已清空");
    }

    /**
     * 購物車商品數量-1
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> sub (@RequestBody ShoppingCart shoppingCart){
        log.info("購物車商品數量減1 {}",shoppingCart);
        ShoppingCart subCart = shoppingCartService.sub(shoppingCart);
        return Result.success(subCart);
    }
}
