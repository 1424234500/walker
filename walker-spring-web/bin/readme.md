###mvn 指令依赖环境变量 需要和 配置的pom里面的相同  否则使用eclipse自带install也可
###打包
mvn clean package -Dmaven.test.skip=true

###前端打包压缩 dist
# 进入前端项目目录
cd src/main/resources/vue

# 安装环境
npm install --registry=https://registry.npm.taobao.org
# 启动服务 开发
npm run dev
# 打包压缩
npm run build:prod

##前端vue模块配置
1.事件触发调用 this.$store.dispatch('user/login', this.loginForm).then
2.模块方法配置 /store/modules/userSocket.js 配置导入api import { login, logout, getInfo } from '@/api/shiro'
3.实现api

###部署 复制
/walker-web/release/*
/walker-web/release/conf/*
/walker-web/release/lib/*

[ ssh 免密码配置key上传注册 ]
方案1 自动差异化增量压缩打包 上传 备份 解压覆盖 重启
./deploy.sh
方案2 git钩子的方式云端自动打包部署？

###启动
./ipModel.sh




###打包&上传
./do [ web | core ]


