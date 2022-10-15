``

死信队列：无法被消费的消息
应用场景：
为了保证订单消息数据不丢失，当消息消费异常时，将消息放入死信队列
订单未支付

死信来源：
消息ttl过期，队列满了，消息被拒绝 

示例架构：
生产者 -> 正常直接交换机 -> routingKey(zhangsan) -> 普通队列 
-> c1
如果 消息ttl过期、队列满了、消息被拒绝， -> 死信直接交换机 -> routingKey(lisi) -> 死信队列 -> c2

====
等待新建
延迟队列，死信队列中的ttl过期
示例架构：
生产者 -> 正常直接交换机 -> routingKey(zhangsan) -> 普通队列 -> 消息ttl过期 -> 死信直接交换机 -> routingKey(lisi) -> 死信队列 -> c2
延迟队列特点：
队列内有序
使用场景：
1.订单在十分钟之内未支付则自动取消（这类订单数据适合放在mq中，大量的订单消息不适合放在redis，不能被公共使用，mq适合堆积大量此类消息）
2.新创建的店铺，如果在十天内都没有上传过商品，则自动发送消息提醒。（用定时任务需要生成大量定时任务，不如放入一个队列，统一处理）
3.用户注册成功后，如果三天内没有登陆则进行短信提醒。（现在看，这种类似一个时间段要干啥的，又不是每天定点执行的，用队列是最合适的）
4.用户发起退款，如果三天内没有得到处理则通知相关运营人员。
5.预定会议后，需要在预定的时间点前十分钟通知各个与会人员参加会议
``