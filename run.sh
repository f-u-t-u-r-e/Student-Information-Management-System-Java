#!/bin/bash
# 运行脚本

echo "Student Information Management System is being launched..."

# 检查是否已编译
if [ ! -d "bin" ]; then
    echo "Compilation file not found, compiling..."
    ./compile.sh
fi

# 运行程序
cd bin
java Main
