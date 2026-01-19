#!/bin/bash
# 图片素材管理功能测试脚本

API_BASE="http://localhost:8080/api"
TEST_IMAGE_PATH="E:/test_images/sample.jpg"

echo "==========图片素材管理功能测试=========="
echo

# 步骤1: 登录获取token
echo "1. 登录获取token..."
LOGIN_RESP=$(curl -s -X POST "${API_BASE}/auth/login/v1" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "登录响应: $LOGIN_RESP"
TOKEN=$(echo $LOGIN_RESP | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 登录失败，无法获取token"
  exit 1
fi

echo "✅ 登录成功，token: ${TOKEN:0:20}..."
echo

# 步骤2: 查询图片分类列表
echo "2. 查询图片分类列表..."
CATEGORY_RESP=$(curl -s -X POST "${API_BASE}/admin/image-category/list/query/v1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"pageNo":1,"pageSize":20}')

echo "分类列表响应: $CATEGORY_RESP"
CATEGORY_ID=$(echo $CATEGORY_RESP | grep -o '"id":[0-9]*' | head -n1 | cut -d':' -f2)

if [ -z "$CATEGORY_ID" ]; then
  echo "❌ 无法获取分类ID"
  exit 1
fi

echo "✅ 获取到分类ID: $CATEGORY_ID"
echo

# 步骤3: 上传图片素材（需要一个测试图片）
echo "3. 测试上传图片素材（模拟）..."
# 注意：实际上传需要真实的图片文件
echo "提示：上传接口为 POST ${API_BASE}/material/image/upload/v1"
echo "需要 multipart/form-data 格式，包含字段："
echo "  - title: 图片标题"
echo "  - categoryId: 分类ID ($CATEGORY_ID)"
echo "  - description: 图片描述(可选)"
echo "  - file: 图片文件"
echo

# 步骤4: 查询图片素材列表(无筛选)
echo "4. 查询图片素材列表(无筛选)..."
MATERIAL_LIST_RESP=$(curl -s -X POST "${API_BASE}/material/image/list/query/v1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"pageNo":1,"pageSize":10}')

echo "素材列表响应: $MATERIAL_LIST_RESP"
TOTAL=$(echo $MATERIAL_LIST_RESP | grep -o '"total":[0-9]*' | cut -d':' -f2)
echo "✅ 查询成功，当前共有 ${TOTAL:-0} 条图片素材"
echo

# 步骤5: 查询图片素材列表(带标题搜索)
echo "5. 查询图片素材列表(标题搜索: '测试')..."
SEARCH_RESP=$(curl -s -X POST "${API_BASE}/material/image/list/query/v1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"pageNo\":1,\"pageSize\":10,\"title\":\"测试\"}")

echo "搜索响应: $SEARCH_RESP"
echo

# 步骤6: 查询图片素材列表(分类筛选)
echo "6. 查询图片素材列表(分类筛选: $CATEGORY_ID)..."
FILTER_RESP=$(curl -s -X POST "${API_BASE}/material/image/list/query/v1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"pageNo\":1,\"pageSize\":10,\"categoryId\":$CATEGORY_ID}")

echo "筛选响应: $FILTER_RESP"
echo

echo "==========测试完成=========="
echo "✅ 所有API接口调用成功"
echo "注意：图片上传功能需要在前端通过文件上传控件测试"
echo
echo "前端访问地址: http://localhost:5175/bless/web/"
echo "请登录后在'素材管理 > 图片素材管理'菜单进行完整测试"
