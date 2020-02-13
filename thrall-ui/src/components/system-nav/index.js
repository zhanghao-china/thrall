import React from "react";
import { Layout, Menu, Icon } from 'antd';
import { withRouter } from 'react-router-dom';
const { Sider } = Layout;


const nav = (props) => {
  const getPathname = () => {
    if (props.location.pathname === "/real-time-monitoring") {
      return ["2"];
    }
    return ["1"];
  }
  return (
    <Sider
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
      }}
    >
      <div className="logo">THRALL</div>
      <Menu theme="dark" mode="inline" defaultSelectedKeys={getPathname()}
          onSelect={(keyObj)=> {
            if (keyObj.key === '1') {
              props.history.push("/history-exception")
            }
            if (keyObj.key === '2') {
              props.history.push("/real-time-monitoring")
            }
          }}
        >
        <Menu.Item key="1">
          <Icon type="video-camera" />
          <span className="nav-text">历史异常</span>
        </Menu.Item>
        <Menu.Item key="2">
          <Icon type="cloud-o" />
          <span className="nav-text">实时监控</span>
        </Menu.Item>
      </Menu>
    </Sider>
  )
}

export default withRouter(nav);