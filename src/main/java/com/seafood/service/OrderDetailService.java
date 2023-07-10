package com.seafood.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.seafood.entity.OrderDetail;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderDetailService extends IService<OrderDetail> {
}
