###mvn 指令依赖环境变量 需要和 配置的pom里面的相同  否则使用eclipse自带install也可
###打包
mvn clean package -Dmaven.test.skip=true

###部署 复制
/walker-socket/release/*
/walker-socket/release/conf/*
/walker-socket/release/lib/*

[ ssh 免密码配置key上传注册 ]
方案1 自动差异化增量压缩打包 上传 备份 解压覆盖 重启
./deploy.sh
方案2 git钩子的方式云端自动打包部署？

###启动
./server.sh

###打包&上传
./do [ socket | core ]
