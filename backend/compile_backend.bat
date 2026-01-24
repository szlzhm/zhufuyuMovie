@echo off
REM 编译 zhufuyuMovie 后端服务
REM 该脚本使用 Maven Wrapper 编译整个项目

echo Compiling zhufuyuMovie Backend Service...

REM 检查是否有Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请确保已安装Java并配置到PATH环境变量中
    pause
    exit /b 1
)

REM 检查Maven Wrapper是否存在
if not exist "mvnw.cmd" (
    echo 错误: 未找到mvnw.cmd文件，请确保在正确的backend目录下运行此脚本
    pause
    exit /b 1
)

echo 正在编译项目...
echo 注意：首次编译可能需要下载依赖，耗时较长，请耐心等待...

REM 使用Maven Wrapper编译项目
call mvnw.cmd clean package -DskipTests

if %errorlevel% equ 0 (
    echo 项目编译成功！
    echo 编译后的文件位于 target 目录
    dir target /B
) else (
    echo 项目编译失败，错误代码: %errorlevel%
)

pause