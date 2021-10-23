#!/bin/bash
###########################################
#
#一些常用简单功能脚本
#配置启动脚本命令cmd
#配置日志文件路径fileLog
#配置pid grep参数greparg
#配置说明about

#部署路径
dir_proj=$(cd `dirname $0`; pwd -LP)     #/walker/walker-socket
cd ${dir_proj}
#项目名
name_proj=${dir_proj##*/}  #walker-socket
echo "部署路径 dir_proj ${dir_proj}"
echo "项目名 name_proj ${name_proj}"

##-----------------------------------------
jarf="${name_proj}-0.0.1.jar"
echo "jar文件 $jarf"
cmd="java -jar ${jarf}"
fileLog="/home/walker/logs/${name_proj}.log"
var=${fileLog%/*}
[ ! -d ${var} ] && mkdir -p ${var}
taillog='tail -n 10 -f '"$fileLog"

filePid=${dir_proj}/run.pid
fileService=${name_proj}.service
#shutdown the process by the grep pids by the cmd name  Warning ! the space
greparg=${jarf}
about="
    Ctrl the server start/stop/log/pid/help.    \n
    Usage:
    ./server.sh [ start | stop | restart | log | pid | help ] [other args]   \n
        \t  test   \t  test server with stdout    \n
        \t  start   \t  start server with log/system.log  支持自启动centos67  \n
        \t  stop    \t  stop server kill pid    \n
        \t  restart \t  stop & start    \n
        \t  log \t  tailf log/*.log \n
        \t  pidInfo \t  ps -elf | grep server   \n
        \t  help    \t  show this   \n
"



##------------------------------------------
function start(){
    bootup true

    ids=`pid`
    if [[ "${ids}" != "" ]]
    then
        echo 'have beening started'
    else
        line
        echo nohup $cmd '>/dev/null 2>&1 & echo $! > '${filePid}
        nohup $cmd >/dev/null 2>&1 & echo $! > ${filePid}
        echo 'self pid $$='$$
        echo 'jar pid  $!='`cat ${filePid}`
    fi
    pidInfo
}
function test(){
    stop
    tcmd=" $cmd  "	# 日志输出
    line
    echo ${tcmd}
    eval ${tcmd}

}
function stop(){
    bootup false
    ids=`pid`
    tcmd="kill ${ids}"
    line
    echo ${tcmd}
    eval ${tcmd}
    pidInfo
}



# 控制开机启动
# bootup name /xx/xx.sh true (start stop restart)
function bootup(){
    local serviceName=$1
    local shUrl=$2
    local enable=$3

    is7=`cat /etc/issue | grep -i 'CentOS.*7\.[0-9]\+' `
    if [ ! -z "${is7}" ]; then
#centos 7
      echo "make auto start boot ${is7}"
      systemd_dir='/usr/lib/systemd/system'
      [ ! -d ${systemd_dir} ] && ( mkdir -p ${systemd_dir} ;  echo "mkdir ${systemd_dir}" )
      if [ -f ${systemd_dir}/${serviceName}.service ]; then
        doShell "systemctl disable ${serviceName}.service"
        doShell "rm ${systemd_dir}/${serviceName}.service"
      fi
      doShell "systemctl daemon-reload"

      echo "[Unit]
Description=${serviceName}
After=network-online.target remote-fs.target nss-lookup.target
Wants=network-online.target

[Service]
Type=forking
ExecStart=${shUrl} start
ExecReload=${shUrl} restart
ExecStop=${shUrl} stop
KillMode=none

[Install]
WantedBy=multi-user.target
" > ${systemd_dir}/${serviceName}.service

      if [[ "${enable}" == "true" ]]; then
        doShell "systemctl enable ${serviceName}.service"
        doShell "systemctl status ${serviceName}.service"
      else
        echo 'no start'
      fi

    else
#centos 6
      echo "make auto start boot not centos7 " `cat /etc/issue`
      if [ -f "/etc/init.d/${serviceName}" ]; then
        doShell "chkconfig  ${serviceName} off"
        doShell "rm /etc/init.d/${serviceName}"
      fi
      echo "#!/bin/bash
#chkconfig: 2345 80 40
#description: ${serviceName}
${shUrl} start
" > ${shUrl}.chkconfig.start.sh
      doShell "ln -s ${shUrl}.chkconfig.start.sh  /etc/init.d/${serviceName}"
      if [[ "${enable}" == "true" ]]; then
        doShell "chkconfig --add ${serviceName}"
        doShell "chkconfig  ${serviceName} on"
      else
        echo 'no start'
      fi
    fi

    echo 'make auto start over '
    return 0
}



function restart(){
    stop
    start
}

function log(){
    line
    echo ${taillog}
    eval ${taillog}
}

function pidInfo(){
    local ids=`pid`
    if [ ! -z ${ids} ]; then
        ps -ef | grep "$greparg" | grep -v grep  | grep "${ids}"
    fi
    if [ -z "${ids}" ]; then
        echo "stoped"
    else
        echo "running ${ids}"
    fi
}
function pid(){
    local ids=''
    local idsFromFile=''
    [ -f "${filePid}" ] && idsFromFile=`cat ${filePid} `
    if [ ! -z ${idsFromFile} ]; then
      ids=`ps -elf | grep "$greparg" | grep -v grep  | grep "${idsFromFile}" | awk '{print $4}' `
      if [ -z "${ids}" ]; then
        [ -f "${filePid}" ] && rm ${filePid}
      fi
    fi
    echo ${ids}
}
function help(){
    line
    echo -e ${about}
    line
}
function line(){
    echo "---------------------------------"
}

method=$1
out ">>cmd   : "$@
out ">>shAt  : "$(cd `dirname $0`; pwd -LP)
out ">>args  : "'size:$#='"$#"
out ">>method: ${method}"
params=()	# (${rootParams[@]:1})  #重构数组空格问题 1 - n
childArgs=" "
echo "param.0: \"${0}\" "
echo "param.1: \"${1}\" "
for ((i=2; $# > 1; i++)) ; do
	item=${2}
	params[${i}]=${item}
	echo "param.${i}: '${item}' "
	childArgs="${childArgs} '${item}' "	#"包含 避免空格
	shift 1	#移位遍历 因为无法 ${${i}} ($#)减一，而变量值提前一位
done

echo ">>method params.size: ${#params[@]}"
echo ">>method cmd: ${method} ${childArgs}"

toolsLine
if [ -n "$method" ]; then
	eval ${method} ${childArgs}	#二次解析?
else
  help
fi

