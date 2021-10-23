## walker's project

	想到什么写点什么

#### 使用Maven多模块项目结构分离

    mvn clean package -Dmaven.test.skip=true
    mvn clean install -Dmaven.test.skip=true

#### 端口分配

    环境火墙8090+
    8090    walker-web
    8091    webservice
    8092    tomcat  dubbo-monitor
    8093    socket netty
    8094    service provider    swagger
    8095    dubbo-provider-port 
    8096    zookeeper-port
    8097    kafka-port
    8098    mysql 
    8099    vue node proxy

## 编程习惯


异常	？	状态值返回  
异常多使用非受检异常 异常上抛  
实体类	？	map/bean  
如何实现实体类的set/get操作而实际存储为map/bean 便捷实现层级json  


自定义实现	？	spring插件  
接口抽象多种实现方式  

web - socket - android  
数据通信统一json 数据隔离 是否使用序列化？暂不  
android收到数据后 解析数据到实体类 存储数据库 再接着用实体类做业务处理  
android[Object->db->] -json- socket-server[mysql-redis-Object->] -json- android[db->Object->]   

表名 字段名一定大写 查询要取别名 兼容oracle mysql sqlite的sql语法  

框架选型：  
专业的软件干专业的事情：nginx做反向代理，db做固化，cache做缓存，mq做通道  


### 日志级别

参考 https://dubbo.gitbooks.io/dubbo-dev-book/principals/robustness.html	8	设计原则  
WARN 表示可以恢复的问题，无需人工介入。	定期查看  
ERROR 表示需要人工介入问题。				严重 程序退出	报警监控  
出问题时的现场信息 ip 用户 参数 异常栈 并给出可能的原因和解决方案?   
避免重复无意义日志  

### 集群部署

端   |   协议   |   代理   |   服务器群   |   数据库   |   业务   
--- |   --- |   --- |   --- |   --- |   ---
android   |   socket | f5/nginx-socket |  | 消息收发
|   |   |  |   server-socket1 | | dubbo-consumer 
|   |   |  | sserver-socket2 | | dubbo-consumer
browser   | http | f5/nginx-web |  | | 文件上传下载 
|   |   |  | server-web1 | | dubbo-consumer 
|   |   |  | server-web2 | | dubbo-consumer 
|   |   | zookeeper  
|   |   |   | service-provider1 | mysql/redis |dubbo-provider
|   |   |   | service-provider2 | mysql/redis | dubbo-provider



	redis cluster模式 三主三备
	./src/redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 --cluster-replicas 1
	
	mysql 消息和记录分表
	W_MSG       W_MSG_0		    W_MSG_1
	W_MSG_USER  W_MSG_USER_0	W_MSG_USER_1	W_MSG_USER_2	W_MSG_USER_3

# 模块划分

## walker-core

核心模块 简单java项目 公用工具 和数据结构发布订阅简单实现


#### core 核心组件

* annotation 自定义注解实现Test db

* cache 抽象缓存模块 并实现了cache Map的浏览数据结构 用于浏览器的缓存监控

  Map 实现 并实现了url模式存取 eg: map1.list[2].map3.key1 = v1 Redis 实现 只支持了string-string string-string[]的这两种实现 Ehcache 实现

* database 数据库模块 原生jdbc工具类 List<Map> 形式  

  Pool 连接池接口 PoolC3p0Impl c3p0实现 RedisMgr redis连接池案例工具

* scheduler 定时器模块 负责定时器任务管理 使用quartz实现

* service 远程服务模块

  service 用于暴露的通用接口api serviceImpl 实现类 webservice webservice的提供和调用案例 rmi java rmi实现的提供和调用案例 dubbo dubbo实现的提供和调用案例 使用了
  zookeeper和redis注册中心 dubbo-admin-2.5.7.war监控中心

## walker-service

抽象接口 数据操作 基本公用mode 可导出jar于各端使用  

## walker-service-provider

使用springboot搭建的web项目 浏览器使用swagger用于接口测试  
http://localhost:8085/swagger-ui.html  
使用dubbo/springcloud提供服务化接口  
具体实现service模块 用于socket/web模块存储调用  

使用jpa做对象操作  
使用jdbcTemplate做简单sql操作

集群部署  
挂载  公用注册中心  
通信  远程调用  

## walker-socket

socket模块 简单java项目 使用原生socket和Netty框架实现即时通信

集群部署  
挂载  公用redis  
通信  发布订阅  

#### socket 网络组件

集群部署
挂载  f5/nginx
通信  无

* client 模拟客户端  
ClientUI java swing GUI图形化客户端模拟 ClientTest10NoUI 模拟多用户并发压测

* server 使用Netty重新构造的服务端  
job 任务调度 常用于统计监控数据 netty.handler Netty处理器 plugin 业务处理插件 配置化 session 会话管理 Msg 统一传递消息类

## walker-web

java web 项目 使用spring mvc hibernate mybatis 实现oracle数据处理

集群部署
挂载  f5/nginx
通信  无	

#### web 网络服务servlet提供

* controller 控制器

  BaseController 基本抽象类 可继承快捷实现单表配置化增删查改 FileController 文件控制 提供文件的上传下载修改和共享文件夹的浏览 ClassController 类反射控制
  提供package的浏览和实例化调用 TableController 抽象化实现表的增删查改 即把表名也参数化 结合dml ddl语句 实现数据库的远程控制 Page 分页参数类 Context
  结合ThreadLocal实现请求处理上下文

* dao 数据存取 分别以hibernate和mybatis实现了通用型List<Map>结构的数据查询和修改

* event
  ``
  intercept 拦截器 登录拦截 日志拦截 环绕监控 listener 系统启动监听OnLoad task 定时任务调度器
  ``


* 前端 vue adminLTE 






