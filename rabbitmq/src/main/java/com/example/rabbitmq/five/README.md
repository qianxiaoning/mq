``
不公平分发

预取值
prefetch

轮询，预取值满了后跳过

// 定义信道上允许的未确认消息的最大数量
int prefetchCount = 5;
channel.basicQos(prefetchCount);



``