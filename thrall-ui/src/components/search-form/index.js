import React, {
  useState,
  forwardRef,
  useRef,
  useImperativeHandle,
  useCallback 
} from "react";
import { Form,  Select, DatePicker, Button  } from 'antd';
import { useInit } from './service'
import { fromJS } from 'immutable'
import moment from 'moment'
const FormItem = Form.Item;
const { Option } = Select;
const { RangePicker } = DatePicker;


export default forwardRef((props,ref) => {
  const searchRef = useRef()
  const [services] = useInit()
  const [param, setParam] = useState(fromJS({
    startTime: '',
    endTime: '',
    thrallType: '',
    microService: {
      id: ''
    }
  }))
  const params =  useCallback(() => param.toJS(),[param])
  useImperativeHandle(ref, () => {
    return {
      params
    }
  });
  return (
    <div ref={searchRef} style={{paddingBottom: 20,textAlign: 'left'}}>
      <Form layout="inline">
        <FormItem label="服务名称">
          <Select defaultValue=""  onSelect={(val) => setParam(page => page.setIn(['microService','id'],val)) } style={{ width: 120 }}>
              <Option value="">全部</Option>
              {services && services.map(service => <Option key={service.id} value={service.id}>{service.microServiceName}</Option>)}
          </Select>
        </FormItem>
        <FormItem label="时间范围">
          <RangePicker onChange={(dates) => {
            dates = fromJS(dates).map(date => moment(date).format("YYYY-MM-DD"))
            setParam(param => param.set('startTime',dates.first()).set('endTime',dates.last()))
          }} />
        </FormItem>
        <FormItem>
          <Button type="primary" onClick={() => props.onSearch()}>查询</Button>
        </FormItem>
      </Form>
    </div>
  )
})


