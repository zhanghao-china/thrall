import {
  findThrallPage,
  execute
} from './api'
import React, {
  useEffect,
  useState,
  useRef
} from "react";
import {
  fromJS
} from 'immutable'
import { Button,message } from 'antd';
const init = async (param) => {
  const {
    context,
    code
  } = await findThrallPage(param);
  if (code === 200) {
    context.pagination = {
      pageSize: context.size,
      total: context.totalElements,
      current: context.number + 1,
    }
    return context
  }
  return null
}

const useInit = () => {
  const [pageParam,setPageParam] = useState(fromJS({
    pageNow: 0,
    pageSize: 10,
    thrall: null
  }))
  const searchRef = useRef(null)
  const [page, setPage] = useState(fromJS({}))
  const [resVisiable, setResVisiable] = useState(false)

  const onSearch = () => {
    const param = searchRef.current.params()
    setPageParam(pageParam => pageParam.set("thrall",param))
  }

  const [drawerVisible,setDrawerVisible] = useState(false)
  const onClose = () => {
    setDrawerVisible(false)
    setResVisiable(false);
  }

  const setThrallRecordProperties = ({key,value}) => {
    const flag = fromJS(thrallRecord).set(key,value);
    setThrallRecord(flag.toJS());
  }

  const setFormatJson = (json) => {
    let format = null;
    if (!json) {
      return json;
    }
    try {
      format = JSON.stringify(JSON.parse(json),null,6)
    } catch (e) {
      message.error('json格式错误！请检查！')
      format = json;
    }
    return format;
  }

  const executeException = async() => {
    setResVisiable(false);
    const res = await execute(thrallRecord);
    setResVisiable(true);
    setThrallRecordProperties({key: 'executeRes',value: JSON.stringify(res.context)})
  }
  const [thrallRecord, setThrallRecord] = useState(null)
  useEffect(() => {
    init(pageParam).then(res => {
      const {
        size,
        totalElements,
        number
      } = res;
      const pagination = {
        pageSize: size,
        total: totalElements,
        current: number + 1,
        onChange: (currentPage, pageSize) => {
          setPageParam(pageParam => pageParam.set("pageNow", currentPage - 1).set("pageSize",pageSize))
        }
      }
      const columns =  [{
        title: '服务名称',
        dataIndex: 'microService.microServiceName',
        key: 'microServiceName',
        ellipsis: true
      },
      {
        title: '服务类型',
        dataIndex: 'microService.microServiceType',
        key: 'microServiceType',
        width: 90,
        render: (text) => <span> {text === 1 ? "注册" : "直连" } </span>,
      },
      {
        title: 'IP',
        dataIndex: 'hostMessage.host',
        key: 'host',
        ellipsis: true
      },
      {
        title: '端口',
        dataIndex: 'hostMessage.port',
        key: 'port',
        width: 70,
      },
      {
        title: '异常类',
        dataIndex: 'exceptionClassName',
        key: 'exceptionClassName',
        width: 250,
      },
      {
        title: '异常方法',
        dataIndex: 'exceptionMethodName',
        key: 'exceptionMethodName',
        width: 200,
      },
      {
        title: '方式',
        dataIndex: 'reqType',
        key: 'reqType',
        ellipsis: true,
      },
      {
        title: '路径',
        dataIndex: 'reqUrl',
        key: 'reqUrl',
        ellipsis: true,
      },
      {
        title: '参数',
        dataIndex: 'reqParam',
        key: 'reqParam',
        ellipsis: true
      },
      {
        title: '时间',
        dataIndex: 'createTime',
        key: 'createTime',
        ellipsis: true,
      },
      {
        title: '操作',
        key: 'action',
        render: (record) => (
          <Button 
            type="link"
            onClick={() => {
              setDrawerVisible(true)
              setThrallRecord({
                reqHead: JSON.stringify(record.reqHead),
                url: `http://${record.hostMessage.host}:${record.hostMessage.port}${record.reqUrl}`,
                reqType: record.reqType,
                reqParam: record.reqParam,
                microServiceName: record.microService.microServiceName
              })
            }}>
            调试
          </Button>
        ),
      }
    ]
      const pageInfo = fromJS(res).set('columns', columns).set("pagination",pagination)
      setPage(pageInfo)
    })
  }, [pageParam]);

  return [page,searchRef,onSearch,drawerVisible,onClose,thrallRecord,setThrallRecordProperties,setFormatJson,executeException,resVisiable];
};


export {
  useInit
}