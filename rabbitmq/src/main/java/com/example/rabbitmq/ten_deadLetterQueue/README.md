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

``