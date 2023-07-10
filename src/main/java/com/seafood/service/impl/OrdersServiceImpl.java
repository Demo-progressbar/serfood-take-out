package com.seafood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.common.BaseContext;
import com.seafood.common.CustomException;
import com.seafood.dto.OrdersDto;
import com.seafood.entity.*;
import com.seafood.mapper.OrdersMapper;
import com.seafood.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper , Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrdersService ordersService;



    /**
     * 購物車下單
     * @param orders
     */
    @Override
    public void submit(Orders orders) {

        //獲得當前用戶Id
        Long userId = BaseContext.getCurrentId();

        //查詢當前用戶購物車數據
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("購物車為空，不能下單");
        }

        //查詢用戶數據
        User user = userService.getById(userId);

        //查詢地址數據
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用戶地址有誤，不能下單");
        }

        long orderId = IdWorker.getId();//生成訂單號

        Double d = new Double("1.5");



        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//總金額
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向訂單表插入一條數據
        this.save(orders);

        //向訂單明細表插入數據，多條數據
        orderDetailService.saveBatch(orderDetails);

        //清空購物車
        shoppingCartService.remove(wrapper);



    }

    /**
     * 用戶訂單列表
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> userPage(Long page, Long pageSize) {

        //查詢訂單資料
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        //創建Page<OrdersDto>
        Page<OrdersDto> pageDto = new Page<>();

        //分頁查詢
        pageInfo = ordersService.page(pageInfo);

        //拷貝資料
        BeanUtils.copyProperties(pageInfo , pageDto ,"records" );

        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtos = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            //拷貝訂單資料進OrdersDto
            BeanUtils.copyProperties(item ,ordersDto);
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> list = orderDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;

        }).collect(Collectors.toList());

        pageDto.setRecords(ordersDtos);

        return pageDto;
    }

    /**
     * 再來一單
     * @param orders
     */
    @Override
    public void again(Orders orders) {

        //獲取訂單編號
        Long id = orders.getId();
        //查詢Orders表數據
        Orders order = ordersService.getById(id);

        //創建新訂單id
        long orderId = IdWorker.getId();
        //設置id
        order.setId(orderId);
        //設置number
        order.setNumber(String.valueOf(orderId));
        //設置OrderTime
        order.setOrderTime(LocalDateTime.now());
        //設置CheckoutTime
        order.setCheckoutTime(LocalDateTime.now());
        //設置狀態
        order.setStatus(2);
        //保存新訂單
        ordersService.save(order);
        //查詢原訂單細項
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        //將查詢出的結果設置成新訂單明細
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper).stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            //拷貝資料
            BeanUtils.copyProperties(item ,orderDetail ,"id");
            //設置新訂單編號
            orderDetail.setOrderId(order.getId());
            return orderDetail;

        }).collect(Collectors.toList());

        //保存新訂單明細
        orderDetailService.saveBatch(orderDetails);
    }

    /**
     * 員工管理頁面page
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page<Orders> empPage(Long page, Long pageSize, String number, String beginTime, String endTime) {
        //創建Page<Orders>
        Page<Orders> pageInfo = new Page<>(page ,pageSize);

        //設置查詢條件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null ,Orders::getNumber ,number);
        queryWrapper.between(beginTime !=null && endTime !=null,Orders::getOrderTime,beginTime,endTime);

        //分頁查詢
        pageInfo = ordersService.page(pageInfo, queryWrapper);


        return pageInfo;
    }

    /**
     * 修改訂單狀態
     * @param orders
     */
    @Override
    public void status(Orders orders) {

        Orders order = ordersService.getById(orders.getId());

        order.setStatus(orders.getStatus());

        ordersService.updateById(order);

    }
}
