#!/bin/bash
###########################################
#配置目标服务器
#配置目标路径
#按需配置上传内容

#打包路径
root=`pwd -LP`  #/walker/walker-socket/release/
#项目路径
dir_proj=${root%/*}     #/walker/walker-socket
#项目名
name_proj=${dir_proj##*/}  #walker-socket
#[ 项目父级路径 ]
dir_proj_parent=${dir_proj%/*}  #/walker
#临时文件名
tofile_name="${name_proj}.tar.gz"
#临时文件全路径
fromfile_temp=~/temp/${tofile_name}
[ ! -d ~/temp ] && mkdir -p ~/temp

#目标服务器路径
server='walker@39.106.111.11'
todir="/home/walker/${name_proj}"

echo "打包路径 pwd ${root} "
echo "项目名 name_proj ${name_proj}"
echo "项目路径 dir_proj ${dir_proj}"
echo "临时文件全路径 fromfile_temp ${fromfile_temp}"
echo "目标服务器路径 todir ${server} ${todir}"


about="
Ctrl deploy all || just src jar with restart auto or server control  \n
Usage: 
./deploy.sh [ just | all | default ] [server restart|start|log]   \n
./deploy.sh just
./deploy.sh server log  \t  show log
    \t  just \t default only upload src jar    \n
    \t  walker \t only upload walker-* jar    \n
    \t  conf \t upload except lib/*  \n
    \t  all \t upload all jar lib conf    \n

    \t  server \t ctrl server [start | restart | log | pid ]    \n

    \t  help \t show this   \n
"
#压缩 
#[ 备份服务端 ] 
#上传
#解压覆盖
#重启

function just(){
	tar -czvf ${fromfile_temp}  *.jar
	upAndTar $@
}
function walker(){
	tar -czvf ${fromfile_temp} lib/walker*
	upAndTar $@
}

function conf(){
	rm lib -r
	tar -czvf ${fromfile_temp}  * 
	upAndTar $@
}

function all(){
	tar -czvf ${fromfile_temp}  *

	echo "all 上传清空旧文件夹  rm ${todir} -r"
    ssh ${server}  " rm ${todir} -r"

	upAndTar $@
}
function upAndTar(){
    #创建日志目录
	ssh ${server}  " [ ! -d ${todir} ] && mkdir -p ${todir}"

    echo "ssh ${server}  \"cd ${todir} && ./server.sh restart \"  "
    #上传
	scp -p ${fromfile_temp}  ${server}:${todir}/${tofile_name}
    #解压&重启
    ssh ${server}  ". /etc/profile &&  cd ${todir} && tar -xvf ${tofile_name} && ./server.sh restart "

}

function server(){
    #解压&重启
	ssh ${server}  ". /etc/profile &&  cd ${todir}  && ./server.sh $@ "
}

function help(){
    line
    echo -e ${about}
    line
}
function line(){
    echo "---------------------------------"
}
function do_main(){
    echo
    ##########################do something yourself
    do_init $@
    echo
}

function do_init(){
    method=$1
    if [[ "${method}" != "" ]]
    then
        rootParams=($@)
        params=(${rootParams[@]:1})
        $method ${params[@]}
    else
        help 
        just
    fi
} 


#start
do_main $@





