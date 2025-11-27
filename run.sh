#!/bin/bash
# 运行脚本

echo "正在启动学生信息管理系统..."

# 检查是否已编译
if [ ! -d "bin" ]; then
    echo "未找到编译文件,正在编译..."
    ./compile.sh
fi

# 运行程序
cd bin
java Main
