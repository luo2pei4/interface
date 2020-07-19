package com.lp.connector.external;

import com.lp.bean.config.RabbitConfigBean;
import com.lp.common.exception.AdapterConnectException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitConnector {

    private static Logger logger = LoggerFactory.getLogger(RabbitConnector.class);

    private String host;

    private Integer port;

    private String userId;

    private String password;

    private String exchange;

    private String queue;

    private String virtualHost;

    private String routingKey;

    private Channel channel;

    private Connection connection;

    private boolean connected = false;

    public void setConnectConfig(RabbitConfigBean rabbitConfigBean) {

        this.host = rabbitConfigBean.getHost();
        this.port = rabbitConfigBean.getPort();
        this.exchange = rabbitConfigBean.getExchange();
        this.queue = rabbitConfigBean.getQueue();
        this.virtualHost = rabbitConfigBean.getVirtualHost();
        this.routingKey = rabbitConfigBean.getRoutingKey();
        this.userId = rabbitConfigBean.getUserId();
        this.password = rabbitConfigBean.getPassword();
    }

    public void connect() throws AdapterConnectException {

        try {

            logger.info("创建RabbitMQ连接");

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(userId);
            factory.setPassword(password);
            factory.setHost(host);
            factory.setPort(Integer.valueOf(port));
            factory.setVirtualHost(virtualHost);

            // 创建上行连接
            connection = factory.newConnection();

            logger.info("连接创建成功.");

            // 创建上行通道
            channel = connection.createChannel();

            logger.info("创建通道成功.");

            // 声明创建配置上行队列
            channel.queueDeclare(queue, true, false, false, null);

            logger.info("队列声明成功.");

            // 将队列与交换器绑定，并设置路由码
            channel.queueBind(queue, exchange, routingKey);

            logger.info("交换机与队列绑定成功.");

            this.connected = true;

        } catch (TimeoutException timeoutException) {

            throw new AdapterConnectException("创建RabbitMQ连接异常", timeoutException);

        } catch (IOException ioException) {

            throw new AdapterConnectException("创建通道队列异常", ioException);

        } catch (Exception exception) {

            throw new AdapterConnectException("连接异常", exception);
        }
    }

    public String receive() throws AdapterConnectException {

        try {

            GetResponse getResponse = channel.basicGet(queue, true);

            if (getResponse != null) {

                byte[] arr = getResponse.getBody();

                if (arr != null && arr.length > 0) {

                    return new String(arr, "UTF-8");
                }
            }

        } catch (Exception exception) {

            throw new AdapterConnectException("消息接收异常", exception);
        }

        return null;
    }

    public void close() {

        try {

            if (channel != null) {

                channel.close();
            }

            if (connection != null) {

                connection.close();
            }

        } catch (Exception exception) {

        } finally {

            channel = null;
            connection = null;
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

}
