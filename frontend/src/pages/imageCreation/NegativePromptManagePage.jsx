import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Modal, Form, message, Space, Tag, Popconfirm } from 'antd';
import { CopyOutlined, PlusOutlined } from '@ant-design/icons';
import { queryNegativePrompts, saveNegativePrompt, deleteNegativePrompt } from '../../services/negativePrompt.js';

const { TextArea } = Input;

/**
 * 负面提示语管理页面
 */
export default function NegativePromptManagePage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState({
    page: 1,
    size: 10,
    content: ''
  });

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    fetchData();
  }, [queryParams]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const resp = await queryNegativePrompts(queryParams);
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

  const handleSearch = (value) => {
    setQueryParams({ ...queryParams, page: 1, content: value });
  };

  const showModal = (record = null) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
    } else {
      setEditingId(null);
      form.resetFields();
    }
    setIsModalVisible(true);
  };

  const handleOk = () => {
    form.validateFields().then(async (values) => {
      try {
        const resp = await saveNegativePrompt({ ...values, id: editingId });
        if (resp.code === 0) {
          message.success('保存成功');
          setIsModalVisible(false);
          fetchData();
        } else {
          message.error(resp.message || '保存失败');
        }
      } catch (error) {
        message.error('操作失败');
      }
    });
  };

  const handleDelete = async (id) => {
    try {
      const resp = await deleteNegativePrompt(id);
      if (resp.code === 0) {
        message.success('删除成功');
        fetchData();
      } else {
        message.error(resp.message || '删除失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(() => {
      message.success('已复制到剪贴板');
    }).catch(() => {
      message.error('复制失败');
    });
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '负面提示语',
      dataIndex: 'content',
      key: 'content',
      ellipsis: true,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 200,
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" icon={<CopyOutlined />} onClick={() => copyToClipboard(record.content)}>复制</Button>
          <Button type="link" onClick={() => showModal(record)}>编辑</Button>
          <Popconfirm title="确定删除吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>负面提示语管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>新增</Button>
      </div>

      <div className="toolbar" style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索负面提示语"
          onSearch={handleSearch}
          style={{ width: 300 }}
          allowClear
          enterButton
        />
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{
          current: queryParams.page,
          pageSize: queryParams.size,
          total: total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, size) => setQueryParams({ ...queryParams, page, size }),
        }}
      />

      <Modal
        title={editingId ? '编辑负面提示语' : '新增负面提示语'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        width={600}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="content"
            label="内容"
            rules={[{ required: true, message: '请输入负面提示语内容' }]}
          >
            <TextArea rows={6} placeholder="例如: low quality, blurry, worst quality..." />
          </Form.Item>
          <Form.Item
            name="remark"
            label="备注"
          >
            <Input placeholder="用途说明" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
