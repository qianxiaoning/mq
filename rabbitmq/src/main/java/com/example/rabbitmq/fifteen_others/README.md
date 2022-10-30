``
其它问题：
一、幂等性问题：
用户购买商品支付，扣款成功，这时网络中断，订单状态没变，用户第二次付钱，钱会再扣一遍

传统场景解决办法：
将数据操作放入事务，发生错误，事务回滚，防止数据不同步

mq消息重复消费：
mq把消息传给消费者，消费者在给mq返回应答时网络中断，所以mq未收到确认消息，
该条消息会重新发给其它的消费者，或者在网络重连后再次发给该消费者，
实际上该消费者已经成功消费过该条消息，造成消费者重复消费
mq幂等问题解决思路：
mq消费者幂等性问题，一般使用全局id，或者唯一标识比如时间戳，或者uuid，或者订单消费者消费mq中mq自身id，
每次消费消息时用该id判断该消息是否已被消费过
消费端的幂等性保障：
海量订单生成的业务高峰期，生产端可能会重复发生消息，这时消费端要实现幂等性，业界主流幂等性有2种方式：
a.唯一id+指位码机制，利用数据库主键去重。b.利用redis的原子性去实现
a.唯一id+指位码机制：
指纹码，利用一些规则，时间戳加服务器给的别的唯一信息码，由系统业务拼接而来，保证唯一性，
利用查询语句查询这个id是否存在在数据库，优势：拼接实现简单，劣势：如果只有单个数据库，会有写入性能瓶颈
b.利用redis的原子性（推荐）：
利用redis实现setnx命令，天然具有幂等性，从而实现不重复消费


二、惰性队列：
消费者宕机下线了，或由于维护关闭了时使用
架构：
订单催付 -> mq（惰性队列：消息保存在内存中还是磁盘上） -> 消费者 -> 发短信
正常情况下消息保存在内存中，惰性队列：消息保存在磁盘上，比较慢
惰性队列使用场景：
消息堆积有100万了，比如消费者关闭了，这时会把消息保存到硬盘上，不会造成mq消息积压，内存崩溃

队列模式：default，lazy（惰性模式）
3.6.0之前版本：可以通过调用channel.queueDeclare方法时在参数中设置，也可以通过policy策略的方式设置，
两种方式都设置的话，policy优先级更高
代码：
Map<String, Object> args = new HashMap<String, Object>();
args.put("x-queue-mode", "lazy");
channel.queueDeclare("myqueue", false, false, false, args);
页面：
新建队列，添加x-queue-mode参数为lazy
内存使用对比：
100百条，1条1k时，默认队列耗内存1.2G，惰性队列耗1.5MB

三、rabbitmq集群：
例子：3台mq，node1（主）,node2（从）,node3（从）
1.修改主机名vi /etc/hostname，node1,node2
2.vim /etc/hosts，各台机器的ip 主机名都登记上
3.使各节点的cookie文件使用的是同一个值，将主机的cookie复制给从机
scp /var/lib/rabbitmq/.erlang.cookie root@192.168.137.131:/var/lib/rabbitmq/.erlang.cookie
4.重启mq和erlang虚拟机，rabbitmq-server -detached
5.在node2，执行
rabbitmqctl stop_app // (rabbitmqctl stop 会将Erlang 虚拟机关闭，rabbitmqctl stop_app 只关闭 RabbitMQ 服务)
rabbitmqctl reset // 重置
rabbitmqctl join_cluster rabbit@bigData101 // 将自己加入1号节点
rabbitmqctl start_app // (只启动应用服务)
6.查看集群状态
rabbitmq cluster_status
Basics当前节点
Disk Nodes总共节点
在web界面，overview-nodes可以看到各节点
7.解除集群节点(在从机执行)，这里不执行
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app
rabbitmqctl cluster_status
在主机执行
rabbitmqctl forget_cluster_node rabbit@node2


四、镜像队列
在主机上的队列，从节点没有，选择设置在从机备份
web端操作：
Admin-右边Policies-Add / update a policy-Definition
设置参数，ha-mode exactly String,ha-params 2 Number, ha-sync-mode automatic String-添加
这样一个节点数据丢失，其它节点数据还在

五、实现高可用的负载均衡：
把factory.setHost("192.168.xxx.xxx")写死的ip换成
Haproxy实现负载均衡，类似nginx，lvs
程序连主机，主机负责对从机的负载均衡，主从有心跳检测机制，实现替换
Haproxy负载均衡搭建过程：
1.下载 haproxy(在 bigData101 和 node2)
  yum -y install haproxy
2.修改 node1 和 node2 的 haproxy.cfg
vim /etc/haproxy/haproxy.cfg
修改ip为当前机器ip
3.在两台节点启动 haproxy
haproxy -f /etc/haproxy/haproxy.cfg
ps -ef | grep haproxy
4.访问地址
http://ip:8888/stats

Keepalived实现双机(主备)热备（如果主机突然宕机或者网卡失效，所有连接都会断开，
引入 Keepalived 它能够通过自身健康检查、资源接管功能做高可用(双机热备)，实现故障转移）
搭建步骤:
1.下载 keepalived
yum -y install keepalived
2.节点 node1 配置文件
vim /etc/keepalived/keepalived.conf
把资料里面的 keepalived.conf 修改之后替换
3.节点 node2 配置文件
需要修改global_defs 的 router_id,如:nodeB
其次要修改 vrrp_instance_VI 中 state 为"BACKUP"；
最后要将priority 设置为小于 100 的值
4.添加 haproxy_chk.sh
(为了防止 HAProxy 服务挂掉之后 Keepalived 还在正常工作而没有切换到 Backup 上，
所以这里需要编写一个脚本来检测 HAProxy 务的状态,当 HAProxy 服务挂掉之后该脚本会自动重启HAProxy 的服务，
如果不成功则关闭 Keepalived 服务，这样便可以切换到 Backup 继续工作)
vim /etc/keepalived/haproxy_chk.sh(可以直接上传文件)
修改权限 chmod 777 /etc/keepalived/haproxy_chk.sh
5.启动 keepalive 命令(node1 和 node2 启动)
systemctl start keepalived
6. 观察 Keepalived 的日志
tail -f /var/log/messages -n 200
7.观察最新添加的 vip
ip add show
8. node1 模拟 keepalived 关闭状态
systemctl stop keepalived
9. 使用 vip 地址来访问 rabbitmq 集群


七、Federation Exchange联邦交换机
如果2个机房相距很远，网络延迟会高
北京客户 -> 深圳mq，延迟高

让北京mq和深圳mq数据一致
步骤：
每个节点都开启2个插件
rabbitmq-plugins enable rabbitmq_federation
rabbitmq-plugins enable rabbitmq_federation_management
刷新在web页面admin右侧看到

数据从上游到下游，上游的交换机同步数据给下游交换机，
1.准备下游节点交换机
2.在上游节点配置
web页面操作：
admin-Add a new upstream-
Name:aa,URL:amqp://admin:123@bigData101
admin-Add / update a policy-
Name:xxx,Pattern:^fed.*,Apply to:Exchanges-
Definition:federation-upstream=aa 上游策略

右侧Federation Status看running状态，表示联邦交换机成功

联邦队列：
1.admin-Add a new upstream（上面已配过）
2.添加policy，admin-Add / update a policy-
Name:xxx,Pattern:^fed.*,Apply to:Queues-
Definition:federation-upstream=aa 上游策略

八、Shovel，和联邦功能类似，数据转发功能，Shovel能够可靠、持续地从一个 Broker 中的队列(作为源端，即source)
拉取数据并转发至另一个 Broker 中的交换器(作为目的端，即 destination)。
消息 -> shovel2_exchange -> routingkey.q2 -> Q2
消息 -> shovel1_exchange -> routingkey.q1 -> Q1 
-> shovel -> Q2 
2台都安装：
rabbitmq-plugins enable rabbitmq_shovel
rabbitmq-plugins enable rabbitmq_shovel_management

admin右侧多了两个
Add a new shovel:
配置Source URL:amqp://admin:123@bigData101 Q1
Destination URL:amqp://admin:123@node2 Q2

Shovel Status，里面有个running表示成功
``