``
交换机
生产者 -> 交换机 ->RoutingKey绑定队列-> 队列（消息只能消费一次） -> 消费者

发布订阅模式（同一个消息被多个消费者消费）
交换机 -> 多个队列 

交换机必须明确知道如何处理消息，是放入单一队列，还是多个队列，还是丢弃，由交换机类型决定
类型：
直接direct（路由类型）,主题topic，标题header，扇出fanout(发布订阅)，无名类型（默认类型，自定义类型）
交换机由routingKey决定发送到哪个队列

临时队列（无持久化，无名队列）
String queueName = channel.queueDeclare().getQueue();// 不起名

扇出示例
生产者 -> 交换机 -> 两个队列 -> 各一个消费者

学到直接交换机开头
https://www.bilibili.com/video/BV1cb4y1o7zz?p=44&spm_id_from=pageDriver&vd_source=3d3abeae3b926364c339460eed1a8b90
``