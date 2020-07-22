package com.lp.connector.internal;

import com.lp.core.KeyAndQueueMapping;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
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

    private String routingKey;

    private String appName;

    private String queueName;

    private boolean connected = false;

    public void setQueueConfig(String appName) {

        this.routingKey = appName;
        this.appName = appName;
    }

    public boolean connect() {

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

            // 添加初始映射
            KeyAndQueueMapping.addInitialMapping(appName);

            logger.info("添加初始映射......success");

            this.connected = true;

        } catch (Exception exception) {

            logger.error("创建内部RabbitMQ队列......failed", exception);

            this.connected = false;

            return false;
        }

        logger.info("创建内部RabbitMQ队列......success");

        return true;
    }

    public Integer sendMessage(String message) {

        try {

            if (channel == null) {

                connect();
            }

            if (connected) {

                channel.basicPublish(exchangeName, routingKey, true, MessageProperties.TEXT_PLAIN, message.getBytes());
                logger.info("发送消息到内部RabbitMQ......success");

                return 1;
            }

        } catch (Exception exception) {

            logger.error("内部消息发送失败.", exception);
            close();
        }

        return 0;
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
            this.connected = false;
        }
    }
}
