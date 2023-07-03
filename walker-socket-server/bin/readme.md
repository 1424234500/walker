### mvn 指令依赖环境变量 需要和 配置的pom里面的相同 否则使用eclipse自带install也可

### 打包

mvn clean package -Dmaven.test.skip=true

### 部署 复制

/walker-socket/release/*

/walker-socket/release/conf/*

/walker-socket/release/lib/*

[ ssh 免密码配置key上传注册 ]

方案1 自动差异化增量压缩打包 上传 备份 解压覆盖 重启

./deploy.sh

方案2 git钩子的方式云端自动打包部署？

### 启动

./server.sh

### 打包&上传

./do [ socket | core ]

### 添加插件接口

修改conf/plugin.json 1.添加plugin 2.在com.walker.socket.server_1.plugin.Plugin中添加名称常量

### Map Bean 键值对 键全大写!

### 所有消息键以Msg常量 所有plugin名以Plugin中常量

### 消息存储设计

收到消息 存入redis个人收件箱队列max512 同时存入分表后的mysql

* 消息实体存储1份 W_MSG
* 消息关联用户多份 W_MSG_USER
* 查询离线消息/历史消息 分页查询

+ 1.上线 查询离线后收到的**所有会话**消息 从已有消息最新时间点2019-01-02 01:01:01:000查询之后 20条 分页查询直到查询结果少于20条 没有更新
  ``使用redis作为缓存队列 先查询队列 结果集合大于20则直接返回 若小于20则从数据库补齐数据20条``

+ 2.翻看历史消息 从**某会话**已有最旧消息2019-01-02 11:01:01:000查询**该会话**之前 20条 分页查询 滚动直到少于20条 没有更旧

### 分表 mysql redis

* 方案1 自定义key键hash分表

* 方案2 mysql db合并
