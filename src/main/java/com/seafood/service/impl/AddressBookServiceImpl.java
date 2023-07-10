package com.seafood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafood.entity.AddressBook;
import com.seafood.mapper.AddressBookMapper;
import com.seafood.service.AddressBookService;
import org.springframework.stereotype.Service;


@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper , AddressBook> implements AddressBookService {
}
