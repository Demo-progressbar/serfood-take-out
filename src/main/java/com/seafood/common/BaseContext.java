package com.seafood.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 *工具類:ThreadLocal設置當前操作(用戶)的ID值
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    /**
     * 新增
     * @param id
     */
    public static void setCurrentId (Long id){

        threadLocal.set(id);

    }

    /**
     * 修改
     * @return
     */
    public static Long getCurrentId (){
        return threadLocal.get();
    }
}
