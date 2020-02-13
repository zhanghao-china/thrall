import React, { Fragment } from "react";
import SearchForm from '../search-form'
import ExceptionTable from '../exception-table'
import {useInit} from './service'
import { Drawer,Form,Input,Button,message } from 'antd';
import RealTimeMonitoring from '../../components/real-time-monitoring' 
const Item = Form.Item
const { TextArea } = Input;
export default () => {
  const [page,searchRef,onSearch,drawerVisible,onClose,thrallRecord,setThrallRecordProperties,setFormatJson,executeException,resVisiable] = useInit()
  return (
    <Fragment>
      <SearchForm ref={searchRef} onSearch={onSearch}/>
      <ExceptionTable 
        columns={page.get("columns")}
        dataSource={page.get("content")? page.get("content").toJS():[]} 
        pagination={page.get("pagination")}
      />
      {thrallRecord && <Drawer
          width={640}
          title="Thrall异常调测"
          placement="right"
          closable={true}
          onClose={onClose}
          visible={drawerVisible}
        > 
          <Form layout='vertical'>
            <Item label="URL:">
              <Input value={thrallRecord.url}
                onChange={(e) => setThrallRecordProperties({key: 'url', value: e.target.value})}/>
            </Item>
            <Item label="Http Method">
              <Input value={thrallRecord.reqType} onChange={(e) => setThrallRecordProperties({key: 'reqType', value: e.target.value})}/>
            </Item>
            <Item label="Headers">
              <TextArea rows={6} 
                value={setFormatJson(thrallRecord.reqHead)}
                onChange={(e) => setThrallRecordProperties({key: 'reqHead', value: e.target.value})} />
            </Item>
            <Item label="Query">
              <TextArea rows={6} 
                value={setFormatJson(thrallRecord.reqParam)}
                onChange={(e) => setThrallRecordProperties({key: 'reqParam', value: e.target.value})} />
            </Item>
            {
              resVisiable &&  
              <Item label="Result">
                <TextArea rows={6} 
                  value={setFormatJson(thrallRecord.executeRes)}
                />
              </Item>
            }
            {
              resVisiable && 
              <Item label="ExceptionLog">
                <RealTimeMonitoring 
                  microServiceName={thrallRecord.microServiceName} 
                  reqUrl={thrallRecord.url}
                  reqParam={thrallRecord.reqParam}
                />
              </Item>
            }
            <Button type="primary" size='large' 
              onClick={() => {
                try {
                  JSON.parse(thrallRecord.reqParam);
                  JSON.parse(thrallRecord.reqHead);
                } catch(e) {
                  message.error('json格式错误！请检查！')
                  return;
                }
                executeException()
              }}>
              执行
            </Button>
          </Form>
        </Drawer>}
        
    </Fragment>
  )
}


