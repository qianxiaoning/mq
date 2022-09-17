``
简单队列模式

单个生产者，单个消费者

producer -> Connection(信道) -> Broker(rabbitmq,包含exchange交换机，queue队列) -> Connection -> consumer
``