package com.seafood.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 異常處理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //標記要處裡的異常種類
    public Result<String> exceptionHandler (SQLIntegrityConstraintViolationException ex){
        //Duplicate entry
        log.info("攔截到異常了 {}",ex.getMessage());

        //根據異常類型做處理
        if(ex.getMessage().contains("Duplicate entry")){
            //提取錯誤訊息, 用空格分割
            String[] split = ex.getMessage().split(" ");
            return Result.error(split[2]+"已存在");
        }

        return Result.error("未知錯誤");

    }

    /**
     * 關聯異常處理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class) //標記要處裡的異常種類
    public Result<String> exceptionHandler (CustomException ex){
        //Duplicate entry
        log.info("攔截到異常了 {}",ex.getMessage());


        return Result.error(ex.getMessage());

    }
}
