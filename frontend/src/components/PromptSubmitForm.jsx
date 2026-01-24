import React, { useState, useEffect, useMemo } from 'react';
import { Form, Select, Input, InputNumber, Slider, Checkbox, Button, Card, Divider, message, Row, Col, Space, Switch, Radio, Alert, Spin } from 'antd';
import { PlusOutlined, MinusCircleOutlined, LoadingOutlined } from '@ant-design/icons';
import { listAllActiveTemplates, submitImageTask } from '../services/imageCreation.js';
import { listAllNegativePrompts } from '../services/negativePrompt.js';
import DelayedHoverPreview from './DelayedHoverPreview.jsx';
import { useMessage } from './Dialog.jsx';

const { Option } = Select;
const { TextArea } = Input;

/**
 * 提示语提交表单组件（可复用于页面和弹窗）
 * @param {Object} props
 * @param {number} props.initialTemplateId 初始选中的模板ID
 * @param {Function} props.onSuccess 提交成功后的回调
 */
export default function PromptSubmitForm({ initialTemplateId, onSuccess }) {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [templates, setTemplates] = useState([]);
  const [negativePrompts, setNegativePrompts] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [variables, setVariables] = useState({});
  const [inputMode, setInputMode] = useState('template'); // 'template' 或 'direct'
  const [directPromptContent, setDirectPromptContent] = useState('');
  const [errorMessage, setErrorMessage] = useState(''); // 错误信息
  const [submitting, setSubmitting] = useState(false); // 提交中状态
  
  // 使用项目现有的对话框组件
  const dialog = useMessage();

  // 分辨率选项 (Qwen-Image2512 支持的比例)
  const resolutionOptions = [
    { label: '1:1 (1024x1024)', value: '1024*1024' },
    { label: '9:16 (832x1536)', value: '832*1536' },
    { label: '16:9 (1536x832)', value: '1536*832' },
    { label: '4:3 (1216x912)', value: '1216*912' },
    { label: '3:4 (912x1216)', value: '912*1216' },
    { label: '9:16 (684x1216)', value: '684*1216' },
    { label: '21:9 (1920x832)', value: '1920*832' },
    { label: '1:1 (512x512)', value: '512*512' },
    { label: '9:16 (416x768)', value: '416*768' },
    { label: '16:9 (768x416)', value: '768*416' },
    { label: '4:3 (608x456)', value: '608*456' },
    { label: '3:4 (456x608)', value: '456*608' },
    { label: '9:16 (342x608)', value: '342*608' },
    { label: '21:9 (960x416)', value: '960*416' },
  ];

  useEffect(() => {
    fetchTemplates();
    fetchNegativePrompts();
  }, []);

  // 当外部传入 initialTemplateId 时，自动选中模板
  useEffect(() => {
    if (initialTemplateId && templates.length > 0) {
      handleTemplateChange(initialTemplateId);
      form.setFieldsValue({ templateId: initialTemplateId });
    }
  }, [initialTemplateId, templates]);

  const fetchTemplates = async () => {
    try {
      const resp = await listAllActiveTemplates();
      if (resp.code === 0) {
        setTemplates(resp.data);
      } else {
        message.error(resp.message || '获取模板列表失败');
      }
    } catch (error) {
      message.error('网络请求失败');
    }
  };

  const fetchNegativePrompts = async () => {
    try {
      const resp = await listAllNegativePrompts();
      if (resp.code === 0) {
        setNegativePrompts(resp.data);
      }
    } catch (error) {
      console.error('Failed to fetch negative prompts', error);
    }
  };

  const handleTemplateChange = (templateId) => {
    const template = templates.find(t => t.id === templateId);
    setSelectedTemplate(template);
    
    // 初始化变量
    const vars = {};
    if (template && template.placeholderKeywords) {
      const keywords = template.placeholderKeywords.split(',').map(k => k.trim()).filter(k => k);
      keywords.forEach(k => {
        vars[k] = '';
      });
    }
    setVariables(vars);
    
    // 加载模板预设参数
    if (template && template.parameters) {
      const params = template.parameters;
      const formValues = {
        promptContent: template.templateContent,
        resolution: params.resolution || '912*1216',
        numImages: params.numImages || 1,
        seed: params.seed || -1,
        smartOptimization: params.smartOptimization === 1 || params.smartOptimization === true,
        inferenceSteps: params.inferenceSteps || 4,
        cfgScale: params.cfgScale || 1.0,
        enableCustomParams: params.enableCustomParams || false
      };
      
      if (params.customParams) {
        formValues.customParamsList = Object.entries(params.customParams).map(([name, value]) => ({ name, value }));
      } else {
        formValues.customParamsList = [{ name: '', value: '' }];
      }
      
      form.setFieldsValue(formValues);
    } else {
      form.setFieldsValue({ promptContent: template ? template.templateContent : '' });
    }
  };

  const handleVariableChange = (key, value) => {
    const newVars = { ...variables, [key]: value };
    setVariables(newVars);
  };

  // 生成最终预览提示语
  const previewPrompt = useMemo(() => {
    if (inputMode === 'direct') {
      return directPromptContent;
    }
    
    if (!selectedTemplate) return form.getFieldValue('promptContent') || '';
    
    let content = selectedTemplate.templateContent;
    Object.keys(variables).forEach(key => {
      const val = variables[key] || `{{${key}}}`;
      // 全局替换 {{KEYWORD}}
      const regex = new RegExp(`\\{\\{${key}\\}\\}`, 'g');
      content = content.replace(regex, val);
    });
    return content;
  }, [inputMode, directPromptContent, selectedTemplate, variables]);

  const onFinish = async (values) => {
    // 验证提示语内容
    if (!previewPrompt || previewPrompt.trim() === '') {
      message.error('请输入或生成提示语内容');
      return;
    }
    
    // 清除之前的错误信息
    setErrorMessage('');
    setSubmitting(true);
    setLoading(true);
    
    try {
      // 转换自定义参数
      let customParams = null;
      if (values.enableCustomParams && values.customParamsList) {
        customParams = {};
        values.customParamsList.forEach(item => {
          if (item.name && item.name.trim()) {
            customParams[item.name.trim()] = item.value || '';
          }
        });
      }

      const submitData = {
        templateId: inputMode === 'template' ? values.templateId : null,
        promptContent: previewPrompt,
        negativePrompt: values.negativePrompt,
        resolution: values.resolution,
        numImages: values.numImages,
        seed: values.seed,
        smartOptimization: values.smartOptimization ? 1 : 0,
        inferenceSteps: values.inferenceSteps,
        cfgScale: values.cfgScale,
        enableCustomParams: values.enableCustomParams || false,
        customParams: customParams
      };

      const resp = await submitImageTask(submitData);
      
      if (resp.code === 0) {
        // 成功：使用现有的confirm对话框
        const continueSubmit = await dialog.confirm({
          title: '提交成功',
          content: '任务已成功提交，是否继续提交新的任务？',
          okText: '继续提交',
          cancelText: '关闭'
        });
        
        if (continueSubmit) {
          // 用户选择继续提交：重置表单
          form.resetFields();
          setVariables({});
          setSelectedTemplate(null);
          setErrorMessage('');
          setInputMode('template');
          setDirectPromptContent('');
          // 重新设置默认值
          form.setFieldsValue({
            resolution: '912*1216',
            numImages: 1,
            seed: -1,
            smartOptimization: false,
            inferenceSteps: 4,
            cfgScale: 1.0,
            enableCustomParams: false,
            customParamsList: [{ name: '', value: '' }],
            negativePrompt: '低分辨率，低画质，肢体畸形，手指畸形，画面过饱和，蜡像感，人脸无细节，过度光滑，画面具有AI感。构图混乱。文字模糊，扭曲。'
          });
          message.success('已重置表单，请继续提交');
        } else {
          // 用户选择关闭：调用成功回调（如果是在弹窗中，可能需要关闭）
          if (onSuccess) onSuccess();
        }
      } else {
        // 失败：使用现有的error对话框
        await dialog.error(
          resp.message || '提交任务失败，请检查输入信息后重试',
          '提交失败'
        );
        // 设置错误信息到页面顶部
        setErrorMessage(resp.message || '提交任务失败');
      }
    } catch (error) {
      // 异常：使用现有的error对话框
      console.error('Submit task error:', error);
      await dialog.error(
        error.message || '网络异常或服务器错误，请稍后重试',
        '提交异常'
      );
      // 设置错误信息到页面顶部
      setErrorMessage(error.message || '提交任务时发生异常');
    } finally {
      setSubmitting(false);
      setLoading(false);
    }
  };

  return (
    <Spin 
      spinning={submitting} 
      tip="正在提交任务，请稍候..." 
      size="large"
      indicator={<LoadingOutlined style={{ fontSize: 48 }} spin />}
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        initialValues={{
          resolution: '912*1216',
          numImages: 1,
        seed: -1,
        smartOptimization: false,
        inferenceSteps: 4,
        cfgScale: 1.0,
        enableCustomParams: false,
        customParamsList: [{ name: '', value: '' }],
        negativePrompt: '低分辨率，低画质，肢体畸形，手指畸形，画面过饱和，蜡像感，人脸无细节，过度光滑，画面具有AI感。构图混乱。文字模糊，扭曲。'
      }}
    >
      {/* 错误信息提示 */}
      {errorMessage && (
        <Alert
          message="提交失败"
          description={errorMessage}
          type="error"
          closable
          onClose={() => setErrorMessage('')}
          style={{ marginBottom: 16 }}
          showIcon
        />
      )}
      
      <Row gutter={24}>
        {/* 左侧：模板选择与变量输入 */}
        <Col span={14}>
          <Card title="内容设置" bordered={false}>
            {/* 输入方式选择 */}
            <Form.Item label="输入方式">
              <Radio.Group 
                value={inputMode} 
                onChange={(e) => setInputMode(e.target.value)}
                buttonStyle="solid"
              >
                <Radio.Button value="template">从模板生成</Radio.Button>
                <Radio.Button value="direct">直接输入</Radio.Button>
              </Radio.Group>
            </Form.Item>

            {/* 从模板生成模式 */}
            {inputMode === 'template' && (
              <>
                <Form.Item
                  name="templateId"
                  label="选择提示语模板"
                >
                  <Select 
                    placeholder="请选择模板" 
                    onChange={handleTemplateChange}
                    allowClear
                  >
                    {templates.map(t => (
                      <Option key={t.id} value={t.id}>{`#${t.id} - ${t.templateContent.substring(0, 30)}...`}</Option>
                    ))}
                  </Select>
                </Form.Item>

                {selectedTemplate && selectedTemplate.placeholderKeywords && (
                  <Card type="inner" title="填写占位符内容" style={{ marginBottom: 16 }}>
                    {selectedTemplate.placeholderKeywords.split(',').map(k => k.trim()).filter(k => k).map(keyword => (
                      <Form.Item 
                        key={keyword} 
                        label={keyword}
                        required
                      >
                        <Input 
                          placeholder={`请输入${keyword}`} 
                          value={variables[keyword]}
                          onChange={(e) => handleVariableChange(keyword, e.target.value)}
                        />
                      </Form.Item>
                    ))}
                  </Card>
                )}
              </>
            )}

            {/* 直接输入模式 */}
            {inputMode === 'direct' && (
              <Form.Item
                label="提示语内容"
                required
              >
                <TextArea 
                  rows={6}
                  placeholder="请直接输入你想要生成的图片的描述..."
                  value={directPromptContent}
                  onChange={(e) => setDirectPromptContent(e.target.value)}
                />
              </Form.Item>
            )}

            <Form.Item
              label="最终提示语预览"
              required
            >
              <DelayedHoverPreview content={previewPrompt}>
                <div style={{ 
                  padding: '12px', 
                  background: '#f9fafb', 
                  border: '1px solid #d9d9d9', 
                  borderRadius: '4px',
                  minHeight: '100px',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-all'
                }}>
                  {previewPrompt || <span style={{ color: '#bfbfbf' }}>请选择模板或输入内容</span>}
                </div>
              </DelayedHoverPreview>
            </Form.Item>

            <Form.Item
              name="promptContent"
              label="提示语原始文本 (仅供参考)"
              hidden
            >
              <TextArea rows={4} readOnly />
            </Form.Item>

            <Form.Item
              name="negativePrompt"
              label="负面提示语"
            >
              <TextArea rows={3} placeholder="描述不希望出现的元素" />
            </Form.Item>

            <Form.Item label="快捷填充负面提示语">
              <Select 
                placeholder="从库中选择负面提示语" 
                onChange={(val) => form.setFieldsValue({ negativePrompt: val })}
                allowClear
              >
                {negativePrompts.map(n => (
                  <Option key={n.id} value={n.content}>{n.remark ? `${n.remark}: ${n.content.substring(0, 30)}...` : n.content.substring(0, 50)}</Option>
                ))}
              </Select>
            </Form.Item>
          </Card>
        </Col>

        {/* 右侧：模型参数设置 */}
        <Col span={10}>
          <Card title="参数设置" bordered={false}>
            <Form.Item
              name="resolution"
              label="图像分辨率"
              rules={[{ required: true, message: '请选择分辨率' }]}
            >
              <Select placeholder="请选择分辨率">
                {resolutionOptions.map(opt => (
                  <Option key={opt.value} value={opt.value}>{opt.label}</Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              label="生成图片数量"
              name="numImages"
            >
              <InputNumber min={1} max={4} style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              label="随机种子 (-1 为随机)"
              required
            >
              <Row gutter={16}>
                <Col span={16}>
                  <Form.Item name="seed" noStyle>
                    <Slider 
                      min={-1} 
                      max={10000} // 滑块限制在10000以内，方便拖动
                      tooltip={{ formatter: (val) => val === -1 ? '随机' : val }}
                    />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="seed" noStyle>
                    <InputNumber
                      min={-1}
                      max={99999999} // 输入框允许更大的范围
                      style={{ width: '100%' }}
                    />
                  </Form.Item>
                </Col>
              </Row>
            </Form.Item>

            <Form.Item
              name="smartOptimization"
              valuePropName="checked"
            >
              <Checkbox>开启提示语智能优化</Checkbox>
            </Form.Item>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="inferenceSteps"
                  label="推理步骤"
                >
                  <InputNumber min={1} max={100} style={{ width: '100%' }} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="cfgScale"
                  label="CFG (提示词相关性)"
                >
                  <InputNumber min={0.1} max={20.0} step={0.1} style={{ width: '100%' }} />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item
              name="enableCustomParams"
              label="启用自定义参数"
              valuePropName="checked"
            >
              <Switch />
            </Form.Item>

            <Form.Item noStyle shouldUpdate={(prevValues, curValues) => prevValues.enableCustomParams !== curValues.enableCustomParams}>
              {({ getFieldValue }) => 
                getFieldValue('enableCustomParams') && (
                  <Card type="inner" title="自定义参数" size="small" style={{ marginBottom: 16 }}>
                    <Form.List name="customParamsList">
                      {(fields, { add, remove }) => (
                        <>
                          {fields.map(({ key, name, ...restField }) => (
                            <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                              <Form.Item
                                {...restField}
                                name={[name, 'name']}
                                rules={[{ required: true, message: '参数名不能为空' }]}
                              >
                                <Input placeholder="参数名" style={{ width: 120 }} />
                              </Form.Item>
                              <Form.Item
                                {...restField}
                                name={[name, 'value']}
                              >
                                <Input placeholder="参数值" style={{ width: 150 }} />
                              </Form.Item>
                              <MinusCircleOutlined onClick={() => remove(name)} />
                            </Space>
                          ))}
                          <Form.Item>
                            <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                              添加参数
                            </Button>
                          </Form.Item>
                        </>
                      )}
                    </Form.List>
                  </Card>
                )
              }
            </Form.Item>

            <Divider />

            <div style={{ textAlign: 'center' }}>
              <Button 
                type="primary" 
                size="large" 
                htmlType="submit" 
                loading={loading}
                disabled={submitting}
                style={{ width: '100%', height: '50px', fontSize: '18px' }}
              >
                立即生成
              </Button>
            </div>
          </Card>
        </Col>
      </Row>
    </Form>
  </Spin>
  );
}
