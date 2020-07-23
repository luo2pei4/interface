package com.lp.controller;

import com.alibaba.fastjson.JSONObject;
import com.lp.common.utils.StringUtil;
import com.lp.connector.internal.InternalConnector;
import com.lp.core.InternalConnectorManagement;
import com.lp.core.Register;
import com.lp.common.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 解析应用注册API
 * 
 * @author luo2p
 */
@RestController
@RequestMapping("/adapter")
public class RegistController {

	private static Logger logger = LoggerFactory.getLogger(RegistController.class);

	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public JSONObject regist(@RequestBody JSONObject registInfo) {

		JSONObject jsonObject = new JSONObject();

		if (registInfo != null) {

			String ipAddress = registInfo.getString("ipAddress");
			String serverPort = registInfo.getString("port");
			String appName = registInfo.getString("appName");

			logger.info(">>>>>>>>>>>>>>>>>>>> 收到注册请求 <<<<<<<<<<<<<<<<<<<<");
			logger.info("应用地址: " + ipAddress);
			logger.info("应用端口: " + serverPort);
			logger.info("应用名称: " + appName);

			// 获取内存中的注册信息
			Map<String, JSONObject> parseAppInfoMap = Register.getParseAppInfoMap();

			if (StringUtil.isEmpty(appName)) {

				// 没有应用名称的情况
				jsonObject.put("status", "DOWN");
				jsonObject.put("message", "没有设置应用名称, 无效的请求.");

				logger.info("没有设置应用名称, 无效的请求.");

			} else if (parseAppInfoMap.containsKey(appName)) {

				// 判断是否有同名的应用已经注册
				jsonObject.put("status", "EXIST");
				jsonObject.put("message", "同名应用已被注册,请调整应用配置文件中的应用名称.");

				logger.info("同名应用已被注册,请调整应用配置文件中的应用名称.");

			} else {

				// 生成新的内部RabbitMQ连接器对象
				InternalConnector internalConnector = (InternalConnector)SpringUtil.getBean("internalConnector");

				// 向对象实例中设置注册应用名称, 用于创建消息队列
				internalConnector.setQueueConfig(appName);

				// 连接内部RabbitMQ, 连接失败的情况, 返回注册失败信息
				if (internalConnector.connect()) {

					// 保存内部RabbitMQ连接器实例
					InternalConnectorManagement.save(appName, internalConnector);

					// 保存注册信息
					Register.save(appName, jsonObject);

					jsonObject.put("status", "OK");
					jsonObject.put("message", "应用注册成功.");
					logger.info(">>>>>>>>>>>>>>>>>>>> 应用注册成功 <<<<<<<<<<<<<<<<<<<<");

				} else {

					jsonObject.put("status", "FAILED");
					jsonObject.put("message", "注册过程中创建内部队列失败.");
				}
			}

		} else {

			jsonObject.put("status", "DOWN");
			jsonObject.put("message", "无效的请求.");
			
			logger.info("无效的请求.");
		}
		
		return jsonObject;
	}
}
