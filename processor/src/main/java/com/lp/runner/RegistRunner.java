package com.lp.runner;

import com.alibaba.fastjson.JSONObject;
import com.lp.service.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(value = 1)
public class RegistRunner implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(RegistRunner.class);

    @Autowired
    private BusinessService businessService;

    @Value("${app.name}")
    private String appName;

    @Value("${regist.url}")
    private String registUrl;

    @Override
    public void run(ApplicationArguments args) {

        JSONObject registInfo = new JSONObject();
        registInfo.put("ipAddress", "localhost");
        registInfo.put("port", "12345");
        registInfo.put("appName", appName);
        registInfo.put("appStatus", "OK");

        RestTemplate restTemplate = new RestTemplate();
        JSONObject registResponse = restTemplate.postForObject(registUrl, registInfo, JSONObject.class);

        if (registResponse != null) {

            String status = registResponse.getString("status");

            if (status.equals("OK")) {

                businessService.start();

            } else {

                // 当注册失败的时候, 退出程序
                // 参数0表示正常退出, 1表示异常退出
                System.exit(1);
            }
        }
    }
}
