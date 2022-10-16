``

延迟队列，死信队列中的ttl过期
示例架构：
生产者 -> 正常直接交换机 -> routingKey(zhangsan) -> 普通队列 -> 消息ttl过期 -> 死信直接交换机 -> routingKey(lisi) -> 死信队列 -> c2
延迟队列特点：
队列内有序
使用场景：
1.订单在十分钟之内未支付则自动取消（这类订单数据适合放在mq中，大量的订单消息不适合放在redis，不能被公共使用，mq适合堆积大量此类消息）
生成订单，放入延迟队列，30分钟到后校验数据库状态，是否支付，未支付，把状态改为超时取消
2.新创建的店铺，如果在十天内都没有上传过商品，则自动发送消息提醒。（用定时任务需要生成大量定时任务，不如放入一个队列，统一处理）
3.用户注册成功后，如果三天内没有登陆则进行短信提醒。（现在看，这种类似一个时间段要干啥的，又不是每天定点执行的，用队列是最合适的）
4.用户发起退款，如果三天内没有得到处理则通知相关运营人员。
5.预定会议后，需要在预定的时间点前十分钟通知各个与会人员参加会议

架构图：
2个交换机，3个队列
生产者p -> 普通交换机（direct）X：
    -> routingKey(XA) -> 普通队列(QA)过期时间10秒
    或
    -> routingKey(XB) -> 普通队列(QB)过期时间40秒
-> 死信交换机（direct）Y -> routingKey(YD)-> 死信队列(QD) -> 消费者c

测试：
靠RabbitmqApplication启动类起
浏览器输入 http://localhost:8080/ttl/sendMsg/xxx

延迟队列优化：造一个适合所有延迟时间的队列
架构图:增加一个QC队列，不设置过期时间，由生产者设置过期时间
2个交换机，4个队列
生产者p -> 普通交换机（direct）X：
    -> routingKey(XA) -> 普通队列(QA)过期时间10秒
    或
    -> routingKey(XB) -> 普通队列(QB)过期时间40秒
    或
    -> routingKey(XC) -> 普通队列(QC)
-> 死信交换机（direct）Y -> routingKey(YD)-> 死信队列(QD) -> 消费者c

测试：
浏览器输入 http://localhost:8080/ttl/sendExpirationMsg/xxx/20000
浏览器输入 http://localhost:8080/ttl/sendExpirationMsg/yyy/2000
结果过期时间为2秒的yyy，并没有在过期时间为20秒的xxx之前出现，还是先xxx再yyy
因为队列是先进先出的，mq只会先检测第一个消息是否过期

mq插件实现延迟队列，解决上述的重大问题
延迟队列插件 rabbitmq_delayed_message_exchange-3.8.0.ez
放入/usr/lib/rabbitmq/lib/rabbitmq_server-3.8.8/plugins/下
安装插件
rabbitmq-plugins enable rabbitmq_delayed_message_exchange
装完稍等一会，刷新web控制台，在add exchange-type中多一个x-delayed-message就表示好了
表示延迟消息不由队列完成，改由交换机完成了
原来：生产者 -> 交换机 -> 队列ttl -> 死信交换机 -> 死信队列 -> 消费者
现在：生产者 -> 延迟交换机（消息停n秒） -> 队列 -> 消费者

测试：
浏览器输入 http://localhost:8080/ttl/sendDelayMsg/xxx/20000
浏览器输入 http://localhost:8080/ttl/sendDelayMsg/yyy/2000
结果：先yyy再出现xxx，结果正确

总结：
类似rabbitmq延迟队列插件的有：
java中的DelayQueue，redis的zset，Quartz，Kafka的时间轮

死信队列作用：
消息至少被消费一次，未处理消息不会丢失
延迟队列插件：
一条通用延迟时间的延迟队列

``