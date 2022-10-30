``

发布确认高级
rabbitmq服务宕机，生产者消息投递失败，导致信息丢失，需要手动处理和恢复
架构图：
生产者 -> 交换机（不在） -> 队列（不在） -> 消费者
改良：
生产者 -> 交换机（未回应）-> 触发确认回调接口（ConfirmCallback） 
生产者 -> 交换机（正常）-> 队列（不正常）->  触发回退接口（ReturnCallback） 

开启确认回调：
1.配置文件开启发布确认模式：
spring.rabbitmq.publisher-confirm-type=correlated
2.实现接口RabbitTemplate.ConfirmCallback
3.发送方定义消息id
4.测试

开启回退接口：
1.配置文件开启发布确认模式：
spring.rabbitmq.publisher-returns=true
2.实现接口RabbitTemplate.ReturnCallback
3.测试

测试：
能跑通的接口：
http://localhost:8080/confirm/sendMessage/xxx
发布确认的调通接口：
http://localhost:8080/confirm/sendMessage2/xxx
交换机收不到消息的，确认回调接口（改错交换机名字）：
http://localhost:8080/confirm/sendMessage3/xxx
队列收不到消息的，回退接口（改routingKey名字为key2，不可路由）：
http://localhost:8080/confirm/sendMessage4/xxx

``