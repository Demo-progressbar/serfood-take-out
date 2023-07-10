package com.seafood.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seafood.common.Result;
import com.seafood.dto.OrdersDto;
import com.seafood.entity.Orders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Transactional
public interface OrdersService extends IService<Orders> {

    /**
     * 購物車下單
     * @param orders
     */
    void submit (Orders orders);

    /**
     * 用戶訂單列表
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrdersDto> userPage(Long page,Long pageSize);

    /**
     * 再來一單功能
     * @param orders
     *
     */
    void again ( Orders orders);

    /**
     * 員工管理頁面page
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<Orders> empPage (Long page , Long pageSize ,String number ,String beginTime ,String endTime);

    /**
     * 修改訂單狀態
     * @param orders
     */
    void status ( Orders orders);
}
