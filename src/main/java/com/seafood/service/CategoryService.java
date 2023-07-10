package com.seafood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seafood.common.Result;
import com.seafood.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 自訂刪除方法
     * @param id
     */
    void remove (Long id) ;

}
