@echo off
REM 运行脚本 - Windows版本 (保持根目录以正确访问 data/ )

echo Student Information Management System is being launched....

REM 检查是否已编译
if not exist "bin" (
    echo Compilation file not found, compiling...
    call compile.bat
)

REM 运行程序 (不再切换到 bin, 避免相对路径 data/ 丢失)
java -cp bin Main

if %errorlevel% neq 0 (
    echo The program encountered an error. Please review the output above.
    pause
)
