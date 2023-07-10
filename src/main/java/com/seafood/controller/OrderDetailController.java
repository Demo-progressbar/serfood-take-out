package com.seafood.controller;


import com.seafood.service.OrderDetailService;
import com.seafood.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 訂單明細
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    OrdersService ordersService;
    @Autowired
    OrderDetailService orderDetailService;


}
