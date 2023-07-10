package com.seafood.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.Result;
import com.seafood.dto.OrdersDto;
import com.seafood.entity.Orders;
import com.seafood.service.OrderDetailService;
import com.seafood.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 訂單
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    OrdersService ordersService;
    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 購物車下單
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit (@RequestBody Orders orders){

        log.info("下單參數 {}" ,orders);

        ordersService.submit(orders);

        return Result.success("下單成功");

    }

    /**
     * 用戶訂單查詢(分頁)
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page<OrdersDto>> userPage (Long page,Long pageSize){
        log.info("userPage參數為 page={} pageSize={}",page,pageSize);

        Page<OrdersDto> ordersDtoPage = ordersService.userPage(page, pageSize);

        return Result.success(ordersDtoPage);
    }

    /**
     * 再來一單
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public Result<String> again (@RequestBody Orders orders){
        log.info("再來一單參數 {}",orders.getId());
        ordersService.again(orders);
        return Result.success("再來一單添加成功");
    }




    /**
     * 員工端訂單管理分頁
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> empPage (Long page , Long pageSize ,String number ,String beginTime ,String endTime) {

        log.info("員工端訂單管理分頁參數 page{}  pageSize{} {} {} {}",page ,pageSize,number ,beginTime ,endTime);

        Page<Orders> ordersPage = ordersService.empPage(page, pageSize, number, beginTime, endTime);

        return Result.success(ordersPage);
    }

    /**
     * 修改訂單狀態
     * @param orders
     * @return
     */
    @PutMapping
    public Result<String> status (@RequestBody Orders orders) {
        log.info("修改訂單狀態參數 ID{} 狀態{}",orders.getId() ,orders.getStatus());

        ordersService.status(orders);
        return Result.success("訂單狀態更改成功");
    }
}
