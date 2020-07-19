package com.lp.service;

import com.alibaba.fastjson.JSONObject;
import com.lp.bean.config.RabbitConfigBean;
import com.lp.connector.external.RabbitConnector;
import com.lp.common.constant.Constants;
import com.lp.core.InternalConnectorManagement;
import com.lp.core.KeyAndQueueMapping;
import com.lp.core.Register;
import com.lp.common.exception.AdapterConnectException;
import com.lp.common.exception.ValidateException;
import com.lp.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitReceiverService extends Thread {

    private static Logger logger = LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private RabbitConnector rabbitConnector;

    private JSONObject config;

    /**
     * 服务控制标志,初始值为停止
     * 0: 启动
     * 1: 暂停
     * 2: 停止
     */
    private Integer serviceControlFlag = 2;

    /**
     * 设置连接信息
     *
     * @param jsonObject 从控制界面获取的配置信息
     * @throws Exception 返回配置信息验证异常
     */
    public void setConfig(JSONObject jsonObject) throws Exception {

        config = jsonObject;

        // 获取RabbitMQ连接配置
        RabbitConfigBean rabbitConfigBean = new RabbitConfigBean();
        rabbitConfigBean.setHost(jsonObject.getString("rabbitHost"));
        rabbitConfigBean.setPort(jsonObject.getInteger("rabbitPort"));
        rabbitConfigBean.setExchange(jsonObject.getString("rabbitExchange"));
        rabbitConfigBean.setQueue(jsonObject.getString("rabbitQueue"));
        rabbitConfigBean.setVirtualHost(jsonObject.getString("rabbitVirtualHost"));
        rabbitConfigBean.setRoutingKey(jsonObject.getString("rabbitRoutingKey"));
        rabbitConfigBean.setUserId(jsonObject.getString("rabbitUserId"));
        rabbitConfigBean.setPassword(jsonObject.getString("rabbitPassword"));

        // 验证配置内容
        validateConfig(rabbitConfigBean);

        // 设置连接器的连接配置
        rabbitConnector.setConnectConfig(rabbitConfigBean);
    }

    @Override
    public void run() {

        logger.info("连接服务开始.");

        while (true) {

            try {

                if (serviceControlFlag.equals(Constants.SERVICE_CONTROL_STOP)) {

                    rabbitConnector.connect();
                    serviceControlFlag = Constants.SERVICE_CONTROL_START;

                } else if (serviceControlFlag.equals(Constants.SERVICE_CONTROL_PAUSE)) {

                    sleep(5000);

                } else {

                    // 此处逻辑比较麻烦
                    // 当服务控制标志为启动时，判断连接器是否连接到了RabbitMQ，如果没有连接到，则将服务控制标志设置为停止。
                    // 当服务控制标志设置为停止时，将马上进行重新连接(第68行的判断, serviceControlFlag.equals(Constants.SERVICE_CONTROL_STOP))
                    if (rabbitConnector.isConnected()) {

                        // 判断是否有注册信息
                        if (Register.getParseAppInfoMap().isEmpty()) {

                            sleep(5000);
                            continue;
                        }

                        String msg = rabbitConnector.receive();

                        // 当获取的消息为null时，暂停1s再重新获取消息
                        if (StringUtil.isEmpty(msg)) {

                            sleep(1000);

                        } else {

                            logger.info("收到消息: " + msg);

                            String key = MessageParseService.getKey(msg);
                            String queueName = null;

                            if (!StringUtil.isEmpty(key)) {

                                queueName = KeyAndQueueMapping.save(key);
                            }

                            if (!StringUtil.isEmpty(queueName)) {

                                InternalConnectorManagement.getInternalConnector(queueName).sendMessage(msg);
                            }
                        }

                    } else {

                        serviceControlFlag = Constants.SERVICE_CONTROL_STOP;
                    }
                }

            } catch (AdapterConnectException adapterConnectException) {

                logger.error(adapterConnectException.getBusinessMessage(), adapterConnectException);

                rabbitConnector.close();
                serviceControlFlag = Constants.SERVICE_CONTROL_STOP;

                try {

                    // 暂停10s后重连
                    sleep(10000);

                } catch (InterruptedException interruptedException) {

                    logger.error("线程休眠异常.", interruptedException);
                }

            } catch (InterruptedException interruptedException) {

                logger.error("线程休眠异常.", interruptedException);
            }
        }
    }

    /**
     * 对配置信息进行校验
     *
     * @param rabbitConfigBean 配置信息
     * @throws ValidateException 校验异常
     */
    private void validateConfig(RabbitConfigBean rabbitConfigBean) throws ValidateException {

    }

    /**
     * 暂停接收外部消息
     */
    public void pause() {

        serviceControlFlag = Constants.SERVICE_CONTROL_PAUSE;
        logger.info("暂停接收消息.....");
    }

    /**
     * 重新开始接收外部消息
     */
    public void restart() {

        serviceControlFlag = Constants.SERVICE_CONTROL_START;
        logger.info("恢复接收消息.....");
    }

    /**
     * 获取连接配置信息
     *
     * @return 连接配置信息
     */
    public JSONObject getConfig() {

        return config;
    }

    /**
     * 获取服务控制标志
     *
     * @return 服务控制标志
     */
    public Integer getServiceControlFlag() {

        return serviceControlFlag;
    }

}
