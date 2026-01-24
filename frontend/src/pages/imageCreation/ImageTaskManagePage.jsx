import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, DatePicker, Card, Space, Tag, Modal, List, Image, message } from 'antd';
import { queryTasks, getTaskDetail } from '../../services/imageCreation.js';
import DelayedHoverPreview from '../../components/DelayedHoverPreview.jsx';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;
const { Option } = Select;

/**
 * 创作任务管理页面
 */
export default function ImageTaskManagePage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState({
    pageNo: 1,
    pageSize: 10,
    taskStatus: undefined,
    prompt: '',
    startTime: null,
    endTime: null,
    filterTimeType: 'CREATED'
  });

  const [detailVisible, setDetailVisible] = useState(false);
  const [currentTask, setCurrentTask] = useState(null);

  useEffect(() => {
    fetchData();
  }, [queryParams.pageNo, queryParams.pageSize]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const params = { ...queryParams };
      if (params.startTime) params.startTime = params.startTime.format('YYYY-MM-DD HH:mm:ss');
      if (params.endTime) params.endTime = params.endTime.format('YYYY-MM-DD HH:mm:ss');
      
      const resp = await queryTasks(params);
      if (resp.code === 0) {
        setData(resp.data.list);
        setTotal(resp.data.total);
      } else {
        message.error(resp.message || '获取数据失败');
      }
    } catch (error) {
      message.error('网络请求失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (queryParams.pageNo === 1) {
      fetchData();
    } else {
      setQueryParams({ ...queryParams, pageNo: 1 });
    }
  };

  const showDetail = async (taskId) => {
    try {
      const resp = await getTaskDetail(taskId);
      if (resp.code === 0) {
        setCurrentTask(resp.data);
        setDetailVisible(true);
      } else {
        message.error(resp.message || '获取详情失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const getStatusTag = (status) => {
    const statusMap = {
      WAITING: { color: 'default', text: '等待中' },
      PROCESSING: { color: 'processing', text: '进行中' },
      COMPLETED: { color: 'success', text: '已完成' },
      FAILED: { color: 'error', text: '失败' },
      CANCELED: { color: 'warning', text: '已取消' },
      PAUSED: { color: 'warning', text: '暂停' },
    };
    const config = statusMap[status] || { color: 'default', text: status };
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { 
      title: '提示语', 
      dataIndex: 'promptContent', 
      key: 'promptContent', 
      ellipsis: true,
      render: (text) => (
        <DelayedHoverPreview content={text}>
          {text}
        </DelayedHoverPreview>
      )
    },
    { title: '分辨率', dataIndex: 'resolution', key: 'resolution', width: 120 },
    { title: '数量', dataIndex: 'numImages', key: 'numImages', width: 80 },
    { title: '状态', dataIndex: 'taskStatus', key: 'taskStatus', width: 100, render: (s) => getStatusTag(s) },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 180 },
    { 
      title: '操作', 
      key: 'action', 
      width: 120, 
      render: (_, record) => (
        <Button type="link" onClick={() => showDetail(record.id)}>查看详情</Button>
      ) 
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>创作任务管理</h2>
      </div>

      <Card bordered={false} style={{ marginBottom: 16 }}>
        <Space wrap>
          <Input 
            placeholder="搜索提示语" 
            style={{ width: 200 }} 
            value={queryParams.prompt}
            onChange={(e) => setQueryParams({ ...queryParams, prompt: e.target.value })}
          />
          <Select 
            placeholder="任务状态" 
            style={{ width: 120 }} 
            allowClear
            value={queryParams.taskStatus}
            onChange={(val) => setQueryParams({ ...queryParams, taskStatus: val })}
          >
            <Option value="WAITING">等待中</Option>
            <Option value="PROCESSING">进行中</Option>
            <Option value="COMPLETED">已完成</Option>
            <Option value="FAILED">失败</Option>
          </Select>
          <RangePicker 
            showTime 
            onChange={(dates) => setQueryParams({ 
              ...queryParams, 
              startTime: dates ? dates[0] : null, 
              endTime: dates ? dates[1] : null 
            })} 
          />
          <Button type="primary" onClick={handleSearch}>查询</Button>
          <Button onClick={() => setQueryParams({ 
            pageNo: 1, 
            pageSize: 10, 
            taskStatus: undefined, 
            prompt: '', 
            startTime: null, 
            endTime: null, 
            filterTimeType: 'CREATED' 
          })}>重置</Button>
        </Space>
      </Card>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{
          current: queryParams.pageNo,
          pageSize: queryParams.pageSize,
          total: total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, size) => setQueryParams({ ...queryParams, pageNo: page, pageSize: size }),
        }}
      />

      <Modal
        title={`任务详情 #${currentTask?.id}`}
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>关闭</Button>
        ]}
        width={800}
      >
        {currentTask && (
          <Space direction="vertical" style={{ width: '100%' }} size="large">
            <Card type="inner" title="基本信息">
              <p><strong>提示语:</strong> {currentTask.promptContent}</p>
              <p><strong>参数:</strong> 分辨率: {currentTask.resolution} | 数量: {currentTask.numImages} | 种子: {currentTask.seed} | 步数: {currentTask.inferenceSteps} | CFG: {currentTask.cfgScale} | 智能优化: {currentTask.smartOptimization === 1 ? '是' : '否'}</p>
              {currentTask.enableCustomParams && currentTask.customParams && (
                <p>
                  <strong>自定义参数:</strong> {Object.entries(currentTask.customParams).map(([k, v]) => (
                    <Tag key={k} color="blue">{k}: {v}</Tag>
                  ))}
                </p>
              )}
              <p><strong>状态:</strong> {getStatusTag(currentTask.taskStatus)} (最后更新: {currentTask.statusChangedTime})</p>
              {currentTask.taskStatus === 'FAILED' && currentTask.errorMessage && (
                <p style={{ color: '#ff4d4f' }}><strong>失败原因:</strong> {currentTask.errorMessage}</p>
              )}
            </Card>

            <Card type="inner" title="生成结果">
              {currentTask.results && currentTask.results.length > 0 ? (
                <div>
                  {currentTask.results.map((result, rIdx) => (
                    <div key={result.id} style={{ marginBottom: 16 }}>
                      {result.generationTime && (
                        <p style={{ marginBottom: 8 }}><strong>生成耗时:</strong> {result.generationTime}s</p>
                      )}
                      <List
                        grid={{ gutter: 16, xs: 2, sm: 3, md: 4 }}
                        dataSource={result.imageUrls || []}
                        renderItem={(url, iIdx) => (
                          <List.Item>
                            <DelayedHoverPreview type="image" content={url}>
                              <Card 
                                hoverable 
                                cover={<Image alt={`result-${rIdx}-${iIdx}`} src={url} />}
                                bodyStyle={{ padding: 0 }}
                              />
                            </DelayedHoverPreview>
                          </List.Item>
                        )}
                      />
                    </div>
                  ))}
                </div>
              ) : (
                <div style={{ textAlign: 'center', color: '#bfbfbf', padding: '20px' }}>
                  {currentTask.taskStatus === 'COMPLETED' ? '未找到结果记录' : '任务尚未完成'}
                </div>
              )}
            </Card>
          </Space>
        )}
      </Modal>
    </div>
  );
}
