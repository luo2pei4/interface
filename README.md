# interface

## adapter用于接收外部消息源的消息
支持从activemq, rabbitmq和kafka消息中间件接入数据。目前仅支持RabbitMQ<br>
程序启动完成后，访问http://{url}:9528/maintenance/index，在界面上选择数据源类型并完成连接配置后，点击启动按钮。<br>
## businessproc用于处理外部消息源的消息
## common是上述两个模块的共通类，包括共通方法，共通异常和静态常量
