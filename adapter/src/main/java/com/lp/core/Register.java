package com.lp.core;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析程序注册器
 * 
 * @author luo2p
 */
public class Register {

	private static Map<String, JSONObject> parseAppInfoMap = new HashMap<>();

	public static synchronized Map<String, JSONObject> getParseAppInfoMap() {

		return parseAppInfoMap;
	}

	public static synchronized void save(String key, JSONObject jsonObject) {

		parseAppInfoMap.put(key, jsonObject);
	}
}
