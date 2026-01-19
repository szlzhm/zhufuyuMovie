#!/bin/bash
# 修复error变量冲突的脚本

for file in EmotionManagePage.jsx ImageCategoryPage.jsx FileConfigPage.jsx; do
  echo "Fixing $file..."
  
  # 1. 修改useMessage解构,避免冲突
  sed -i 's/const { success, error, warning, MessageComponent }/const { success, error: showError, warning, MessageComponent }/' "$file"
  
  # 2. 重命名error状态变量为validationError
  sed -i 's/const \[error, setError\]/const [validationError, setValidationError]/' "$file"
  
  # 3. 替换所有setError调用
  sed -i 's/setError(/setValidationError(/g' "$file"
  
  # 4. 替换error函数调用为showError (但跳过validationError)
  sed -i 's/\([^a-zA-Z]\)error(/\1showError(/g' "$file"
  
  # 5. 替换{error}为{validationError}
  sed -i 's/{error}/{validationError}/g' "$file"
  
  # 6. 修复success((语法错误
  sed -i "s/success(('/success('/g" "$file"
  
  # 7. 修复return中JSX位置
  sed -i '/<MessageComponent \/>/{ N; s/<MessageComponent \/>\n    <div/    <div/; s/<div/<MessageComponent \/>\n      <div/ }' "$file"
  
  # 8. 某些success应该保持,但第119行的error应改为success (启用/禁用成功)
  sed -i 's/showError(`\${action}成功`)/success(`${action}成功`)/' "$file"
  
done

echo "Done!"
