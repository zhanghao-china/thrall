import React from "react";
import { Table } from 'antd';


export default ((props) => {
  return (
    <Table 
      rowKey={record => record.id}
      columns={props.columns} 
      dataSource={props.dataSource}
      pagination={props.pagination}
      expandedRowRender={record => <div>{record.thrallExceptionMsg}</div>}
    />
  )
})