#!/bin/bash
#这里可替换为你自己的执行程序，其他代码无需更改
SHELL_FOLDER=$(dirname $(readlink -f "$0"))
APP_HOME=`cd $SHELL_FOLDER >/dev/null; cd .. >/dev/null;  pwd`
SPRINGBOOT_OUT="$APP_HOME"/log/thrall.out
CONF_PATH="$APP_HOME"/conf/thrall.properties
APP_NAME=thrall-server-1.0.1-RELEASE.jar
APP_PATH="$APP_HOME"/bin/"$APP_NAME"
#使用说明，用来提示输入参数
usage() {
    echo "Usage: sh 脚本名.sh [start|stop|restart|status]"
    exit 1
}

#检查程序是否在运行
is_exist(){
  pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running. pid=${pid} ."
  else
    nohup java -jar $APP_PATH  --spring.config.location=$CONF_PATH > $SPRINGBOOT_OUT 2>&1 &
    echo "${APP_HOME}"
    echo "${APP_PATH}"
    echo "${CONF_PATH}"
    echo "${SPRINGBOOT_OUT}"
    echo "${APP_NAME} start success"
  fi
}

#停止方法
stop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
  else
    echo "${APP_NAME} is not running"
  fi
}

#输出运行状态
status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running. Pid is ${pid}"
  else
    echo "${APP_NAME} is NOT running."
  fi
}

#重启
restart(){
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac
