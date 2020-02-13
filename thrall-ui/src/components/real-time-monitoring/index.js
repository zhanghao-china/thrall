import React, {
  useEffect
} from "react";
import {
  Terminal
} from 'xterm';
import '../../../node_modules/xterm/css/xterm.css'
import {
  FitAddon
} from 'xterm-addon-fit';
import moment from 'moment'
import {
  uuid
} from 'uuidv4'
import { notification } from 'antd';
export default (props) => {
  
  useEffect(() => {
    const term = new Terminal({
      cols: 92,
      rows: 32,
      cursorBlink: true, // 光标闪烁
      cursorStyle: "underline", // 光标样式  null | 'block' | 'underline' | 'bar'
      scrollback: 9999999, //回滚
      tabStopWidth: 8, //制表宽度
      screenKeys: true //
    })
    const connectionId = uuid();
    window.connectionId = connectionId;
    const fitAddon = new FitAddon();
    const source = new EventSource(`/thrall/sse/${connectionId}`);
    const terminalContainer = document.getElementById('terminal-container')
    term.loadAddon(fitAddon)
    term.open(terminalContainer)
    term.write('welcome to thrall !!!\r\n')
    fitAddon.fit()

    const microServiceName = terminalContainer.getAttribute("data-1")
    const reqUrl =  terminalContainer.getAttribute("data-2")
    const reqParam =  terminalContainer.getAttribute("data-3")
    const isOnlyOne = (data) => {
      const url = `http://${data.hostMessage.host}:${data.hostMessage.port}${data.reqUrl}`;
      return microServiceName === data.microService.microServiceName && reqUrl === url && reqParam === data.reqParam;
    }
    
    source.onmessage = (event) => {
      const printToPage = (data) => {
        term.write(`----------服务名:${data.microService.microServiceName}----------\r\n`)
        term.write(`----------主机端口：${data.hostMessage.host}:${data.hostMessage.port}----------\r\n`)
        term.write(`----------时间：${moment(data.createTime).format("YYYY-MM-DD HH:mm:ss.SSS")}----------\r\n`)
        term.write(`----------请求路径：${data.reqUrl}----------\r\n`)
        term.write(`----------请求方式：${data.reqType}----------\r\n`)
        term.write(`----------请求参数：${data.reqParam}----------\r\n`)
        term.write(data.thrallExceptionMsg)
      }
      const data = JSON.parse(event.data);
      if (microServiceName) {
        if (data && data.thrallExceptionMsg && isOnlyOne(data)) {
          printToPage(data)
          source.close()
        }
      } else {
        if (data && data.thrallExceptionMsg) {
          printToPage(data)
        }
      }
    }
    source.onopen = () => {
      console.log("连接已经打开")
      setTimeout(() => {
        const args = {
          message: '会话结束',
          description:
            '连接时间过长，已为您断开连接！',
          duration: 0,
        };
        notification.open(args);
        source.close()
      }, 60*60*1000)
    }
    source.onerror = () => {
      console.log("连接出错")
    }
    return () => {
      const connectionId = window.connectionId;
      if (connectionId) {
        source.close()
      }
    }
  }, [])
  return ( 
    <div id = "terminal-container"
      data-1 = {props.microServiceName}
      data-2 = {props.reqUrl}
      data-3 = {props.reqParam}
      style = {{
        width: '100%',
        height: 650,
        marginTop: 50
      }}
    />
  )
}