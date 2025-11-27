#!/bin/bash
# 编译脚本

echo "Compiling the Student Information Management System..."

# 创建编译输出目录
mkdir -p bin

# 编译所有Java文件
javac -encoding UTF-8 -d bin -sourcepath src src/Main.java src/model/*.java src/service/*.java src/gui/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Run command: ./run.sh"
else
    echo "Compilation failed. Please check the error message."
    exit 1
fi
