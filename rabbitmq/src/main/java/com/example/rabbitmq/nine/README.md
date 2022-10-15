``

topic主题交换机
主题交换机的routing_key不能随意写，必须是一个单词列表，以点号分隔，如a.b.c
*可以代替一个单词，#可以代替零个或多个单词
队列1（Q1）：   *.orange.*
队列2（Q2）：   *.*.rabbit   lazy.#
单纯一个#类似fanout 

``