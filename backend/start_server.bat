@echo off
REM 启动 zhufuyuMovie 后台服务
REM 该脚本使用 Maven Wrapper 运行 Spring Boot 应用

echo Starting zhufuyuMovie Backend Service...
set JAVA_HOME=C:/Java/jdk-17
set PATH=%PATH%;%JAVA_HOME%/bin

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

echo 正在启动服务...
echo 注意：服务启动可能需要一些时间，请耐心等待...

REM 使用Maven Wrapper运行Spring Boot应用
call mvnw.cmd spring-boot:run

if %errorlevel% equ 0 (
    echo 服务启动成功！
) else (
    echo 服务启动失败，错误代码: %errorlevel%
)

pause