import React from 'react';
import Nav from './components/system-nav'
import './App.css';
import { Layout } from 'antd';
import {  Route,Switch  } from 'react-router-dom'
import routers from "./router"
const { Content } = Layout;
const App = () => {
  return (
    <Layout>
    <Nav />
    <Layout style={{ marginLeft: 200 }}>
      {/* <Header style={{ background: '#fff', padding: 0 }} /> */}
      <Content style={{ overflow: 'initial' }}>
        <div style={{ padding: 24, background: '#fff', textAlign: 'center' }}>
              <Switch>
                  {routers.map(router => <Route  key={router.path}  exact={router.exact} path= {router.path} component={router.component}/>)}
              </Switch>
        </div>
      </Content>
      {/* <Footer style={{ textAlign: 'center' }}>Ant Design ©2018 Created by Ant UED</Footer> */}
    </Layout>
  </Layout>
  )
}

export default App;
