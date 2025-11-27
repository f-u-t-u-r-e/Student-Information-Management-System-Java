
# 学生信息管理系统 (Student Information Management System)

![Java](https://img.shields.io/badge/Java-8%2B-blue)
![GUI](https://img.shields.io/badge/Swing-UI-yellow)
![Version](https://img.shields.io/badge/Version-2.1-brightgreen)
![Status](https://img.shields.io/badge/Build-Manual-lightgrey)
![License](https://img.shields.io/badge/License-Education_NonCommercial-orange)

## 项目简介

本系统是一个基于 Java Swing 的学生信息与课程成绩管理平台，支持学生信息增删改查、灵活的课程成绩管理、加权 GPA 计算、专业排名、统计分析、成绩导入、数据备份等功能。适合课程设计、教学演示与基础实践使用。

## 主要功能

- 学生信息管理（添加 / 编辑 / 删除 / 查看）
- 多条件搜索（学号 / 姓名 / 专业 / 班级 / 综合关键字）
- 成绩导入（CSV批量解析，兼容全角逗号）
- Excel 排名导出（专业排名与全部学生排名生成 `.xlsx` 文件）
- 课程管理（单学生课程增删改查、实时GPA更新）
- 专业排名（按专业筛选 + GPA排序）
- GPA加权计算（基于课程学分与成绩）
- 数据统计（性别分布、平均年龄、专业分布）
- 数据持久化（文本文件存储 + 自动保存）
- 备份功能（生成 `.backup` 文件）
- 自定义排序（学号 / 姓名 / 年龄 / GPA）

## 新增亮点（v2.1）

- 课程管理对话框：单独维护某位学生的全部课程条目
- 支持课程增删改清空，实时刷新 GPA 与总学分
- 成绩导入兼容“中文输入法”全角逗号格式
- 改进运行脚本：保持根目录避免相对路径失效
- 更健壮的数据加载与路径回退逻辑

## 技术栈

- 编程语言：Java 8+
- 图形界面：Swing (JFrame / JTable / JDialog / JFileChooser / JMenu)
- 数据存储：UTF-8 文本（自定义 CSV 格式）
- 集合与Stream：ArrayList / HashMap / Comparator / Stream API
- 异常处理：try-with-resources + 输入验证
- 架构分层：model / service / gui / data

## 运行环境

- 操作系统：Windows / Linux / macOS（示例脚本以 Windows 为主）
- JDK：8 及以上版本
- 编码：UTF-8

## 项目结构

```
Student-Information-Management-System-Java/
├── run.bat                       # 运行脚本（保持根目录）
├── compile.bat                   # 编译脚本（Windows）
├── run.sh / compile.sh           # 类Unix脚本(可选)
├── README.md                     # 项目说明
├── data/
│   ├── students.txt              # 学生与课程数据文件
│   ├── students.txt.backup       # 备份（运行后生成）
│   └── scores_import_example.csv # 成绩导入示例
├── src/
│   ├── Main.java                 # 程序入口
│   ├── model/
│   │   ├── Student.java          # 学生实体（含课程列表与GPA计算）
│   │   └── Course.java           # 课程实体（学分+成绩）
│   ├── service/
│   │   ├── FileManager.java      # 文件读写与备份
│   │   └── StudentManager.java   # 学生/成绩业务逻辑
│   └── gui/
│       ├── MainFrame.java            # 主窗口
│       ├── StudentDialog.java        # 学生信息编辑对话框
│       ├── ScoreImportDialog.java    # 成绩导入对话框
│       ├── MajorRankingFrame.java    # 专业排名窗口
│       └── CourseManagementDialog.java # 单学生课程管理（v2.1）
└── 其它文档
```

## 数据文件说明

`students.txt` 每行一个学生（含课程列表）：
```
学号,姓名,性别,年龄,专业,班级,联系电话,[课程1:学分:成绩|课程2:学分:成绩|...]
2021001,张三,男,20,计算机科学与技术,计科2101,13800138001,[高等数学:4.0:85.5|大学英语:3.0:90.0|程序设计基础:4.0:92.0]
```

成绩导入文件（可批量追加课程）：
```
学号,课程名,学分,成绩
2021001,高等数学,4.0,85.5
2021001,大学英语,3.0,90.0
2021002,数据结构,4.0,88.0
```
支持：
```
2021001，高等数学，4.0，100   # 全角逗号也可识别
```

## 安装与编译

方式一：脚本（推荐）
```bash
# Windows
./compile.bat
./run.bat
```

方式二：手动
```bash
cd Student-Information-Management-System-Java
javac -encoding UTF-8 -d bin -sourcepath src src/Main.java src/model/*.java src/service/*.java src/gui/*.java
java -cp bin Main
```

方式三：IDE
1. 使用 IntelliJ IDEA / Eclipse 导入
2. 设置 Project SDK = JDK 8+
3. 运行 `Main.java`

## 使用说明

1. 启动程序：执行 `run.bat`
2. 添加学生：点击“添加学生”填写信息
3. 编辑学生：选中行 → “编辑学生”或双击
4. 删除学生：选中行 → “删除学生”
5. 搜索：选择类型 + 输入关键字 → “搜索”
6. 排序：使用菜单“排序”
7. 查看统计：点击“统计信息”
8. 数据备份：点击“备份数据”生成 `.backup`
9. 导入成绩：点击“导入成绩”→ 选择 CSV → 自动追加课程
10. 专业排名：点击“专业排名”查看排序与统计；可导出当前专业排名为 Excel
11. 导出全部排名：工具栏“导出全部排名”生成所有学生排名 Excel
12. 管理单学生课程：选中学生 → “课程管理”→ 增删改课程

## 课程管理（v2.1）

- 进入方式：选中学生 → “课程管理”
- 支持操作：添加 / 编辑 / 删除 / 清空
- GPA 与总学分实时刷新
- 关闭窗口后主表自动刷新

## 成绩导入

- 自动解析学号匹配已存在学生
- 未匹配学号会提示“学号不存在”
- 支持全角/中文逗号自动标准化
- 导入成功后自动保存并可在课程管理中查看

## 专业排名与导出

专业排名窗口提供：
- 按所选专业筛选学生并按 GPA 降序排列（并列成绩保留同名次）
- 显示总人数、已录入成绩人数、平均 GPA
- 预览排名数据（文本模式）
- 导出当前专业排名为原生 Excel (`.xlsx`) 文件

Excel 导出特性：
- 无需额外第三方库（内置轻量级 XLSX 生成）
- 自动生成表头：排名/学号/姓名/班级/总学分/GPA/课程数 + 所有出现过的课程列
- 课程列按出现顺序统一（不存在课程的单元格留空）
- 数值单元格使用数值类型，便于后续排序与统计
- 支持全体学生排名导出（工具栏“导出全部排名”）

## 统计分析

提供：
- 总人数
- 男/女生人数
- 平均年龄
- 专业分布（分组计数）

## GPA 计算公式

```
GPA = Σ(课程成绩 × 课程学分) / Σ(全部课程学分)
```
若无课程：GPA = 0

## 数据备份与安全

- 备份文件：`students.txt.backup`
- 触发方式：手动“备份数据”
- 建议：重大操作前先备份
- 文件编码：UTF-8

## 输入验证

- 学号：唯一且不可编辑（避免冲突）
- 姓名 / 专业 / 班级：必填
- 年龄：15–100
- 联系电话：11位数字
- 课程学分与成绩：数值型（异常格式会提示）

## 常见问题 FAQ

| 问题 | 可能原因 | 解决方法 |
|------|----------|----------|
| 导入显示“跳过无效行” | 字段数量不等于4 | 确认格式“学号,课程名,学分,成绩” |
| 全角逗号导致失败 | 旧版本不兼容 | 使用 v2.1 或以上版本 |
| GPA 未更新 | 未使用课程管理对话框 | 通过“课程管理”修改课程 |
| 备份无变化 | 未实际变更数据 | 修改学生后再备份 |
| 数据为空 | `students.txt` 无记录 | 添加学生或填充示例数据 |

## 更新日志

### v2.2 (2025-11)
- 新增原生 Excel (`.xlsx`) 排名导出（专业/全部学生）
- 删除旧 CSV 排名导出实现（简化代码）

### v2.1 (2025-11)
- 新增 `CourseManagementDialog` 单学生课程管理
- 成绩导入兼容全角/中文逗号与 BOM
- 运行脚本改进，避免路径偏移
- README 与数据格式说明更新

### v2.0
- 增加成绩导入与专业排名功能
- 学生 CSV 支持课程集合格式

### v1.0
- 基础学生信息管理 + 文件存储

## 未来规划

- Excel / PDF 导出
- 图表可视化（成绩分布）
- 权限与角色管理
- 照片/头像支持
- 多文件自动备份策略

## 贡献

欢迎：
- 提交 Issue / Bug 报告
- 提交新功能 Pull Request
- 优化 UI / 数据结构
- 增加自动化测试

## 免责声明 / 许可

- 仅用于教学与学习交流，禁止商业使用
- 数据文件由使用者自行维护
- 修改与分发请注明来源
- 本系统不保证用于真实生产场景的稳定性

## 联系方式

- Issues: 反馈问题与建议
- Discussions: 功能讨论与扩展

---

如果需要英文版或进一步的功能文档，请提出需求。
- **开发时间**: 2025.11

## 许可

本项目仅用于学习交流,禁止商业使用。
