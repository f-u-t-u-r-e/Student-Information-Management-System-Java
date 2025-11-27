@echo off
REM 编译脚本 - Windows版本

echo Compiling the Student Information Management System...

REM 创建编译输出目录
if not exist "bin" mkdir bin

REM 编译所有Java文件
javac -encoding UTF-8 -d bin -sourcepath src src\Main.java src\model\*.java src\service\*.java src\gui\*.java

if %errorlevel% == 0 (
    echo Compilation successful!
    echo Run command: run.bat
) else (
    echo Compilation failed. Please check the error message.
    pause
    exit /b 1
)
