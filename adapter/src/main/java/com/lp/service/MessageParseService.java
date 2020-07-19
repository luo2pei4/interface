package com.lp.service;

import com.alibaba.fastjson.JSONObject;

public class MessageParseService {

    // 该静态方法用于返回用于判断的业务关键字
    public static String getKey(String msg) {

        // 下面的代码是测试代码
        JSONObject jsonObj = JSONObject.parseObject(msg);
        return jsonObj.getString("an");
    }
}
