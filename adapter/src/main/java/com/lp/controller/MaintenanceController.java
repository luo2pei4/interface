package com.lp.controller;

import com.alibaba.fastjson.JSONObject;
import com.lp.common.constant.Constants;
import com.lp.common.exception.AdapterConnectException;
import com.lp.common.exception.ValidateException;
import com.lp.service.RabbitReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private static Logger logger = LoggerFactory.getLogger(MaintenanceController.class);

    @Autowired
    private RabbitReceiverService rabbitReceiverService;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public JSONObject startService(@RequestBody JSONObject jsonObject) {

        String sourceType = jsonObject.getString("sourceType");

        if (sourceType.equals("2")) {

            try {

                rabbitReceiverService.setConfig(jsonObject);
                rabbitReceiverService.start();

                jsonObject.put("msg", "服务启动成功");
                jsonObject.put("serviceControlFlag", "0");

            } catch (ValidateException validateException) {

                logger.error(validateException.getBusinessMessage(), validateException);
                jsonObject.put("msg", "输入验证失败: " + validateException.getMessage());

            } catch (AdapterConnectException adapterConnectException) {

                logger.error(adapterConnectException.getBusinessMessage(), adapterConnectException);
                jsonObject.put("msg", "连接失败: " + adapterConnectException.getMessage());

            } catch (Exception exception) {

                logger.error("未知异常", exception);
                jsonObject.put("msg", "未知异常: " + exception.getMessage());
            }
        }

        return jsonObject;
    }

    @ResponseBody
    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public JSONObject pauseService(@RequestBody JSONObject jsonObject) {

        String sourceType = jsonObject.getString("sourceType");

        if (sourceType.equals("2")) {

            rabbitReceiverService.pause();
        }

        jsonObject.put("msg", "暂停接收消息");
        jsonObject.put("serviceControlFlag", "1");

        return jsonObject;
    }

    @ResponseBody
    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public JSONObject resumeService(@RequestBody JSONObject jsonObject) {
        String sourceType = jsonObject.getString("sourceType");

        if (sourceType.equals("2")) {

            rabbitReceiverService.restart();
        }

        jsonObject.put("msg", "恢复接收消息");
        jsonObject.put("serviceControlFlag", "0");

        return jsonObject;
    }

    @ResponseBody
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public JSONObject checkService(@RequestBody JSONObject jsonObject) {

        String sourceType = jsonObject.getString("sourceType");

        logger.info("数据源类型: " + sourceType);

        if (sourceType.equals("2")) {

            if (rabbitReceiverService.isAlive()) {

                jsonObject = rabbitReceiverService.getConfig();

                if (rabbitReceiverService.getServiceControlFlag().equals(Constants.SERVICE_CONTROL_START)) {

                    jsonObject.put("msg", "服务启动成功");

                } else if (rabbitReceiverService.getServiceControlFlag().equals(Constants.SERVICE_CONTROL_PAUSE)) {

                    jsonObject.put("msg", "暂停接收消息");
                }

                jsonObject.put("serviceControlFlag", rabbitReceiverService.getServiceControlFlag().toString());

            } else {

                jsonObject.put("serviceControlFlag", "2");
            }
        }

        return jsonObject;
    }
}
