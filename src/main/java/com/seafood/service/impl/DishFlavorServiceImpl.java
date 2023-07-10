package com.seafood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.entity.DishFlavor;
import com.seafood.mapper.DishFlavorMapper;
import com.seafood.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper , DishFlavor> implements DishFlavorService {
}
