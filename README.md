## walker's project  
想到什么写点什么  
平时技术积累 写一些demo或工具组件 记录于此  
后续需要用的时候 可快速捡起来 或者 直接拿去作为工具使用
核心注意依赖性 尽可能减少依赖三方jar 减少环境依赖  
贯彻拿来主义  

#### 使用Maven多模块项目结构分离  
mvn clean package -Dmaven.test.skip=true  
mvn clean install -Dmaven.test.skip=true  

#### 端口分配  
``` 
环境火墙8090+
8090    walker-web
8091    socket http 文件服务 无权限控制
8092    socket http controller接口服务 无权限控制
8093    socket netty
8094    service provider    swagger
8095    dubbo-provider-port 
8096    zookeeper-port
8097    kafka-port
8098    mysql 
8099    vue node proxy
``` 
## 编程习惯 规范  解耦
越基础则越灵活 可定制性越高 但可用性越低 平衡取舍 拆解

问题   |   本项目   |   内部应用/引用jar   |   外部应用/前端/Android 
--- |   --- |   --- |   ---
接口接入 |   直接调用 |   hsf |   http/socket
接口异常 |   抛出 |   抛出 |   状态值 
接口返回 |   model |   model |   json 

表名 字段名一定大写 查询要取别名 兼容oracle mysql sqlite的sql语法  

框架选型：  
专业的软件干专业的事情：nginx做反向代理，db做固化，cache做缓存，mq做通道  


redis cluster模式 三主三备  
./src/redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 --cluster-replicas 1

mysql 消息和记录分表  
W_MSG       W_MSG_0		    W_MSG_1
W_MSG_USER  W_MSG_USER_0	W_MSG_USER_1	W_MSG_USER_2	W_MSG_USER_3

### 日志级别  
参考 https://dubbo.gitbooks.io/dubbo-dev-book/principals/robustness.html	8	设计原则  
WARN 表示可以恢复的问题，无需人工介入。 定期查看    
ERROR 表示需要人工介入问题。 严重 程序退出 报警监控    
出问题时的现场信息 ip 用户 参数 异常栈 并给出可能的原因和解决方案?   
避免重复无意义日志  限流   

业务日志  规范可提取 用于sls kafka等采集分析
如  
19298287287,1,0,a,type_3  

# 模块划分  
公用能力 工具务必写入基础模块 避免重复代码

## walker-core 核心组件  
基础工具类
基础算法
基础功能模块
不依赖于仍和多余的东西 复用 使用callback暴露回执

* annotation 自定义注解 
* mq 参考rocketMq实现简易文件存储mq
* box 工具箱 拿去即用
* demo 测试案例 拿去改改使用
* design 大话设计模式案例改写
* 
## walker-service-client  
数据模型  
抽象接口  
抽象基础父类  
导出jar用于外部系统接入使用规范  
  
## walker-socket-server  
socket server 实现  
提供长连接服务  

集群部署 长连接负载（f5/nginx）于不同server上面  
需依赖server间通信机制（redis广播 or 点对点rpc）  


## walker-spring-core  
springboot 基础父项目模板  
spring系列通用工具 组件 依赖注入 公用  

通用能力  


## walker-spring-provider 微服务具体实现 数据处理层

## walker-spring-web 门面 前端&业务入口 控制层
* controller 控制器  
  BaseController 基本抽象类 可继承快捷实现单表配置化增删查改   
  FileController 文件控制 提供文件的上传下载修改和共享文件夹的浏览 
  ClassController 类反射控制    提供package的浏览和实例化调用  
  TableController 抽象化实现表的增删查改 即把表名也参数化 结合dml ddl语句 实现数据库的远程控制 Page 分页参数类 
  

* dao 数据存取 分别以hibernate和mybatis实现了通用型List<Map>结构的数据查询和修改  

* event  系统生命周期 事件处理  
* 前端 vue adminLTE   






