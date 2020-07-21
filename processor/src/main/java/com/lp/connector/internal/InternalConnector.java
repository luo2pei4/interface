package com.lp.connector.internal;

import com.lp.common.exception.AdapterConnectException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service(value = "internalConnector")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InternalConnector {

    private static Logger logger = LoggerFactory.getLogger(InternalConnector.class);

    private Channel channel;

    private Connection connection;

    @Value("${rabbit.mq.host}")
    private String host;

    @Value("${rabbit.mq.port}")
    private int port;

    @Value("${rabbit.mq.username}")
    private String userName;

    @Value("${rabbit.mq.password}")
    private String password;

    @Value("${rabbit.mq.exchangename}")
    private String exchangeName;

    @Value("${app.name}")
    private String routingKey;

    @Value("${app.name}")
    private String appName;

    private String queueName;

    private boolean connected = false;

    public void connect() {

        try {

            logger.info("开始连接内部RabbitMQ");

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(userName);
            factory.setPassword(password);
            factory.setHost(host);
            factory.setPort(port);

            // 创建上行连接
            connection = factory.newConnection();

            logger.info("创建连接......success");

            // 创建上行通道
            channel = connection.createChannel();

            logger.info("创建通道......success");

            queueName = exchangeName.concat(".").concat(appName);

            // 声明创建配置上行队列
            channel.queueDeclare(queueName, true, false, false, null);

            logger.info("队列声明......success");

            // 将队列与交换器绑定，并设置路由码
            channel.queueBind(queueName, exchangeName, routingKey);

            logger.info("交换机与队列绑定......success");

            logger.info("添加初始映射......success");

            this.connected = true;

        } catch (Exception exception) {

            logger.error("创建内部RabbitMQ队列......failed", exception);

            this.connected = false;
        }

        logger.info("创建内部RabbitMQ队列......success");
    }

    public String receive() throws AdapterConnectException {

        try {

            GetResponse getResponse = channel.basicGet(queueName, true);

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

            logger.info("关闭内部RabbitMQ连接.");

            if (connection != null) {

                connection.close();
            }

            if (channel != null) {

                channel.close();
            }

        } catch (Exception e) {

        } finally {

            connection = null;
            channel = null;
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
