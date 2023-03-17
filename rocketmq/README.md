### rocketmq
#### rocketmq
``
教程内容来自b站尚硅谷免费教程
一.MQ用途
限流削峰
异步解耦
数据收集
二.常见MQ
ActiveMQ：Java语言开发，单机吞吐量万级，早期活跃，现在社区活跃低，现在项目使用少，JMS、STOMP协议
RabbitMQ：ErLang语言开发，单机吞吐量万级，定制化开发难度大，吞吐量较Kafka与RocketMQ低，AMQP、MQTT、STOMP协议
Kafka：Scala/Java语言开发，单机吞吐量十万级，高吞吐率，用于大数据领域的实时计算、日志采集。自研协议，仅支持RabbitMQ与Kafka
RocketMQ：Java语言开发，单机吞吐量十万级，性能与稳定性非常高。自研协议，支持RabbitMQ、Kafka，提倡使用RocketMQ
三.基本概念
1.消息（Message）生产和消费数据的最小单位，每条消息必须属于一个主题。

2.主题（Topic）Topic表示一类消息的集合，每个主题包含若干条消息，每条消息只能属于一个主题，是RocketMQ进行消息订阅的基本单位。
topic:message 1:n message:topic 1:1
一个生产者可以同时发送多种Topic的消息；而一个消费者只对某种特定的Topic感兴趣，即只可以订阅和消费一种Topic的消息。
producer:topic 1:n consumer:topic 1:1

3.标签（Tag）
为消息设置的标签，用于同一主题下区分不同类型的消息。
来自同一业务单元的消息，可以根据不同业务目的在同一主题下设置不同标签。
标签能够有效地保持代码的清晰度和连贯性，并优化RocketMQ提供的查询系统。
消费者可以根据Tag实现对不同子主题的不同消费逻辑，实现更好的扩展性。

Topic是消息的一级分类，Tag是消息的二级分类。
Topic：货物
    tag=上海
    tag=江苏
    tag=浙江
Topic：人
    tag=上海
    tag=江苏
    tag=浙江
    
------- 消费者 -----
topic=货物 tag = 上海
topic=货物 tag = 上海|浙江
topic=货物 tag = *

4.队列（Queue）
存储消息的物理实体。一个Topic中可以包含多个Queue，每个Queue中存放的就是该Topic的消息。
一个Topic的Queue也被称为一个Topic中消息的分区（Partition）。
一个Topic的Queue中的消息只能被一个消费者组中的一个消费者消费。
一个Queue中的消息不允许同一个消费者组中的多个消费者同时消费。

5.分片（Sharding）
分片不同于分区。在RocketMQ中，分片指的是存放相应Topic的Broker。
每个分片中会创建出相应数量的分区，即Queue，每个Queue的大小都是相同的。

6.消息标识（MessageId/Key）
RocketMQ中每个消息拥有唯一的MessageId，且可以携带具有业务标识的Key，以方便对消息的查询。
不过需要注意的是，MessageId有两个：在生产者send()消息时会自动生成一个MessageId（msgId)，当消息到达Broker后，Broker也会自动生成一个MessageId(offsetMsgId)。
msgId、offsetMsgId与key都称为消息标识。

msgId：由producer端生成，其生成规则为：producerIp + 进程pid + MessageClientIDSetter类的ClassLoader的hashCode + 当前时间 + AutomicInteger自增计数器
offsetMsgId：由broker端生成，其生成规则为：brokerIp + 物理分区的offset（Queue中的偏移量）
key：由用户指定的业务相关的唯一标识

四.系统架构
1.Producer
RocketMQ中的消息生产者都是以生产者组（Producer Group）的形式出现的。
生产者组是同一类生产者的集合，这类Producer发送相同Topic类型的消息。一个生产者组可以同时发送多个主题的消息。
2.Consumer
RocketMQ中的消息消费者都是以消费者组（Consumer Group）的形式出现的。
消费者组是同一类消费者的集合，这类Consumer消费的是同一个Topic类型的消息。
消费者组使得在消息消费方面，
实现负载均衡（将一个Topic中的不同的Queue平均分配给同一个Consumer Group的不同的Consumer，注意，并不是将消息负载均衡）
和容错（一个Consmer挂了，该Consumer Group中的其它Consumer可以接着消费原Consumer消费的Queue）
的目标变得非常容易。

消费者组中Consumer的数量应该小于等于订阅Topic的Queue数量。如果超出Queue数量，则多出的Consumer将不能消费消息。
不过，一个Topic类型的消息可以被多个消费者组同时消费。
注意:
1 ）消费者组只能消费一个Topic的消息，不能同时消费多个Topic消息
2 ）一个消费者组中的消费者必须订阅完全相同的Topic

3.Name Server
NameServer是一个Broker与Topic路由的注册中心，支持Broker的动态注册与发现。
主要包括两个功能：
Broker管理：接受Broker集群的注册信息并且保存下来作为路由信息的基本数据；提供心跳检测机制，检查Broker是否还存活。
路由信息管理：每个NameServer中都保存着Broker集群的整个路由信息和用于客户端查询的队列信息。
Producer和Conumser通过NameServer可以获取整个Broker集群的路由信息，从而进行消息的投递和消费。

路由注册
...

路由剔除
...

路由发现
...

客户端NameServer选择策略
...

4.Broker
Broker充当着消息中转角色，负责存储消息、转发消息。
Broker在RocketMQ系统中负责接收并存储从生产者发送来的消息，同时为消费者的拉取请求作准备。
Broker同时也存储着消息相关的元数据，包括消费者组消费进度偏移offset、主题、队列等。
Kafka 0.8版本之后，offset是存放在Broker中的，之前版本是存放在Zookeeper中的。

模块构成:
...

集群部署
...

5.工作流程
1）启动NameServer，NameServer启动后开始监听端口，等待Broker、Producer、Consumer连接。
2）启动Broker时，Broker会与所有的NameServer建立并保持长连接，然后每 30 秒向NameServer定时发送心跳包。
3）发送消息前，可以先创建Topic，创建Topic时需要指定该Topic要存储在哪些Broker上，
当然，在创建Topic时也会将Topic与Broker的关系写入到NameServer中。不过，这步是可选的，也可以在发送消息时自动创建Topic。
4）Producer发送消息，启动时先跟NameServer集群中的其中一台建立长连接，并从NameServer中获取路由信息，
即当前发送的Topic消息的Queue与Broker的地址（IP+Port）的映射关系。
然后根据算法策略从队选择一个Queue，与队列所在的Broker建立长连接从而向Broker发消息。
当然，在获取到路由信息后，Producer会首先将路由信息缓存到本地，再每 30 秒从NameServer更新一次路由信息。
5）Consumer跟Producer类似，跟其中一台NameServer建立长连接，获取其所订阅Topic的路由信息，
然后根据算法策略从路由信息中获取到其所要消费的Queue，然后直接跟Broker建立长连接，开始消费其中的消息。
Consumer在获取到路由信息后，同样也会每 30 秒从NameServer更新一次路由信息。
不过不同于Producer的是，Consumer还会向Broker发送心跳，以确保Broker的存活状态。

Topic的创建模式
...

读/写队列
...

五.单机安装与启动
1.环境要求：64位系统，jdk1.8，maven3.2.x
2.官网下载Binary版本
rocketmq-all-4.8.0-bin-release.zip
上传到服务器，解压
3.修改初始内存（改小）
cd rocketmq-all-4.8.0-bin-release
vim bin/runserver.sh
82行，
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"

vim bin/runbroker.sh
67行，
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m"

4.启动
启动NameServer
nohup sh bin/mqnamesrv &
tail -f ~/logs/rocketmqlogs/namesrv.log

启动broker
服务器保证下面两个路径存在，不存在创建
/root/store/commitlog
/root/store/consumequeue
nohup sh bin/mqbroker -n localhost:9876 &
tail -f ~/logs/rocketmqlogs/broker.log

5.发送/接收消息测试
发送消息
export NAMESRV_ADDR=localhost:9876
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
接收消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer

6.关闭Server
关闭name server和broker
sh bin/mqshutdown broker
sh bin/mqshutdown namesrv

六.安装可视化控制台
1.下载到本地解压打开
https://github.com/apache/rocketmq-externals/archive/refs/tags/rocketmq-console-1.0.0.zip
2.修改配置文件/src/main/resources/application.properties
端口号
server.port=9300
RocketMQ的name server地址
rocketmq.config.namesrvAddr=localhost:9876 // 或者此处填空，设置环境变量export NAMESRV_ADDR="localhost:9876"
3.在pom.xml中添加JAXB依赖
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-core</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
4.package打包
获得包
rocketmq-console-ng-1.0.0.jar
5.启动
nohup java -jar rocketmq-console-ng-1.0.0.jar &
6.访问
http://192.168.xx.xx:9300

七.集群搭建理论
数据复制与刷盘策略
...

``
https://bright-boy.gitee.io/technical-notes/#/rocketmq/index?id=%e7%ac%ac1%e7%ab%a0-rocketmq%e6%a6%82%e8%bf%b0