package com.xiaohe.search2.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ReturnResult<T> {
    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> ReturnResult<T> success(T object) {
        ReturnResult<T> r = new ReturnResult<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> ReturnResult<T> error(String msg) {
        ReturnResult r = new ReturnResult();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public ReturnResult<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}