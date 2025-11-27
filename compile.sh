#!/bin/bash
# 编译脚本

echo "正在编译学生信息管理系统..."

# 创建编译输出目录
mkdir -p bin

# 编译所有Java文件
javac -encoding UTF-8 -d bin -sourcepath src src/Main.java src/model/*.java src/service/*.java src/gui/*.java

if [ $? -eq 0 ]; then
    echo "编译成功!"
    echo "运行命令: ./run.sh"
else
    echo "编译失败,请检查错误信息"
    exit 1
fi
