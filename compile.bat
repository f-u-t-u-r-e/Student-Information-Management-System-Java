@echo off
REM 编译脚本 - Windows版本

echo 正在编译学生信息管理系统...

REM 创建编译输出目录
if not exist "bin" mkdir bin

REM 编译所有Java文件
javac -encoding UTF-8 -d bin -sourcepath src src\Main.java src\model\*.java src\service\*.java src\gui\*.java

if %errorlevel% == 0 (
    echo 编译成功!
    echo 运行命令: run.bat
) else (
    echo 编译失败,请检查错误信息
    pause
    exit /b 1
)
