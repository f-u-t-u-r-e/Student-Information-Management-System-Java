@echo off
REM 运行脚本 - Windows版本

echo 正在启动学生信息管理系统...

REM 检查是否已编译
if not exist "bin" (
    echo 未找到编译文件,正在编译...
    call compile.bat
)

REM 运行程序
cd bin
java Main
cd ..
