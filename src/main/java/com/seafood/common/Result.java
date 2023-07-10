package com.seafood.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回結果類
 * @param <T>
 */
@Data
public class Result<T> {

    private Integer code; //編碼：1成功，0和其他數字為失敗

    private String msg; //錯誤訊息

    private T data; //數據

    private Map map = new HashMap(); //動態數據

    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
