package com.seafood.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.entity.User;
import com.seafood.mapper.UserMapper;
import com.seafood.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper , User> implements UserService {
}
