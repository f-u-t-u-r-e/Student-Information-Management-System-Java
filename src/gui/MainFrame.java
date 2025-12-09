package gui;

import model.Student;
import service.StudentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

/**
 * 主界面窗口
 * 包含学生列表显示、搜索、增删改查等功能
 */
public class MainFrame extends JFrame {
    private StudentManager studentManager;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JLabel statusLabel;

    // 表格列名
    private final String[] columnNames = {"学号", "姓名", "性别", "年龄", "专业", "班级", "联系电话"};

    // 现代化配色方案
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // 主色调 - 蓝色
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // 次色调 - 浅蓝
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);       // 强调色 - 绿色
    private static final Color DANGER_COLOR = new Color(231, 76, 60);        // 危险色 - 红色
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // 背景色 - 浅灰
    private static final Color CARD_COLOR = Color.WHITE;                     // 卡片背景
    private static final Color TEXT_COLOR = new Color(44, 62, 80);           // 文字颜色
    private static final Color HEADER_COLOR = new Color(52, 73, 94);         // 表头颜色

    public MainFrame() {
        // 初始化学生管理器 - 兼容从 bin 目录运行的情况(旧脚本)
        String dataPath = "data/students.txt";
        java.io.File primary = new java.io.File(dataPath);
        if (!primary.exists()) {
            java.io.File fallback = new java.io.File("../data/students.txt");
            if (fallback.exists()) {
                dataPath = "../data/students.txt";
            }
        }
        studentManager = new StudentManager(dataPath);

        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置窗口属性
        setTitle("学生信息管理系统 v2.0");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // 初始化组件
        initComponents();

        // 加载数据
        refreshTable();
    }

    /**
     * 初始化界面组件
     */
    private void initComponents() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // 顶部面板 - 搜索和工具栏
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(CARD_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel searchLabel = new JLabel("搜索类型:");
        searchLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchLabel.setForeground(TEXT_COLOR);
        searchPanel.add(searchLabel);

        searchTypeCombo = new JComboBox<>(new String[]{"全部", "学号", "姓名", "专业", "班级"});
        searchTypeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchTypeCombo.setPreferredSize(new Dimension(120, 32));
        searchPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(searchField);

        JButton searchButton = createStyledButton("搜索", PRIMARY_COLOR);
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);

        JButton refreshButton = createStyledButton("刷新", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> refreshTable());
        searchPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.NORTH);

        // 工具栏面板
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        toolbarPanel.setBackground(CARD_COLOR);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JButton addButton = createStyledButton("+ 添加学生", ACCENT_COLOR);
        addButton.addActionListener(e -> addStudent());
        toolbarPanel.add(addButton);

        JButton editButton = createStyledButton("编辑学生", PRIMARY_COLOR);
        editButton.addActionListener(e -> editStudent());
        toolbarPanel.add(editButton);

        JButton deleteButton = createStyledButton("删除学生", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteStudent());
        toolbarPanel.add(deleteButton);

        toolbarPanel.add(createSeparator());

        JButton statsButton = createStyledButton("统计信息", new Color(155, 89, 182));
        statsButton.addActionListener(e -> showStatistics());
        toolbarPanel.add(statsButton);

        JButton backupButton = createStyledButton("备份数据", new Color(52, 152, 219));
        backupButton.addActionListener(e -> backupData());
        toolbarPanel.add(backupButton);

        toolbarPanel.add(createSeparator());

        JButton importScoreButton = createStyledButton("导入成绩", new Color(230, 126, 34));
        importScoreButton.addActionListener(e -> importScores());
        toolbarPanel.add(importScoreButton);

        JButton rankingButton = createStyledButton("专业排名", new Color(241, 196, 15));
        rankingButton.addActionListener(e -> showMajorRanking());
        toolbarPanel.add(rankingButton);

        JButton manageCourseButton = createStyledButton("课程管理", new Color(22, 160, 133));
        manageCourseButton.addActionListener(e -> manageCourses());
        toolbarPanel.add(manageCourseButton);
        
        JButton exportAllButton = createStyledButton("导出全部排名", new Color(22, 160, 133));
        exportAllButton.addActionListener(e -> exportAllRanking());
        toolbarPanel.add(exportAllButton);

        topPanel.add(toolbarPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 表格
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可直接编辑
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.setRowHeight(35);
        studentTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        studentTable.setSelectionBackground(new Color(52, 152, 219, 100));
        studentTable.setSelectionForeground(TEXT_COLOR);
        studentTable.setGridColor(new Color(189, 195, 199));
        studentTable.setShowGrid(true);
        studentTable.setIntercellSpacing(new Dimension(1, 1));

        // 设置表头样式
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createLineBorder(HEADER_COLOR));

        // 居中对齐
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 双击编辑
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStudent();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // 底部面板 - 状态栏
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(HEADER_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusLabel.setForeground(Color.WHITE);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("学生信息管理系统 v2.0");
        versionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(189, 195, 199));
        bottomPanel.add(versionLabel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 添加到窗口
        add(mainPanel);

        // 菜单栏
        createMenuBar();

        // 搜索框回车搜索
        searchField.addActionListener(e -> performSearch());
    }

    /**
     * 创建样式化按钮
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // 鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * 创建分隔符
     */
    private Component createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 25));
        separator.setForeground(new Color(189, 195, 199));
        return separator;
    }

    /**
     * 创建菜单栏
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");

        JMenuItem refreshItem = new JMenuItem("刷新数据");
        refreshItem.addActionListener(e -> refreshTable());
        fileMenu.add(refreshItem);

        JMenuItem backupItem = new JMenuItem("备份数据");
        backupItem.addActionListener(e -> backupData());
        fileMenu.add(backupItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "确定要退出系统吗?", "确认退出",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        // 编辑菜单
        JMenu editMenu = new JMenu("编辑");

        JMenuItem addItem = new JMenuItem("添加学生");
        addItem.addActionListener(e -> addStudent());
        editMenu.add(addItem);

        JMenuItem editItem = new JMenuItem("编辑学生");
        editItem.addActionListener(e -> editStudent());
        editMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("删除学生");
        deleteItem.addActionListener(e -> deleteStudent());
        editMenu.add(deleteItem);

        // 排序菜单
        JMenu sortMenu = new JMenu("排序");

        JMenuItem sortByIdItem = new JMenuItem("按学号排序");
        sortByIdItem.addActionListener(e -> {
            studentManager.sortByStudentId();
            refreshTable();
            updateStatus("已按学号排序");
        });
        sortMenu.add(sortByIdItem);

        JMenuItem sortByNameItem = new JMenuItem("按姓名排序");
        sortByNameItem.addActionListener(e -> {
            studentManager.sortByName();
            refreshTable();
            updateStatus("已按姓名排序");
        });
        sortMenu.add(sortByNameItem);

        JMenuItem sortByAgeItem = new JMenuItem("按年龄排序");
        sortByAgeItem.addActionListener(e -> {
            studentManager.sortByAge();
            refreshTable();
            updateStatus("已按年龄排序");
        });
        sortMenu.add(sortByAgeItem);

        JMenuItem sortByGPAItem = new JMenuItem("按GPA排序");
        sortByGPAItem.addActionListener(e -> {
            studentManager.sortByGPA();
            refreshTable();
            updateStatus("已按GPA排序");
        });
        sortMenu.add(sortByGPAItem);

        // 成绩菜单
        JMenu scoreMenu = new JMenu("成绩");

        JMenuItem importScoreItem = new JMenuItem("导入成绩");
        importScoreItem.addActionListener(e -> importScores());
        scoreMenu.add(importScoreItem);

        JMenuItem rankingItem = new JMenuItem("专业排名");
        rankingItem.addActionListener(e -> showMajorRanking());
        scoreMenu.add(rankingItem);

        JMenuItem manageCourseItem = new JMenuItem("课程管理(选中学生)");
        manageCourseItem.addActionListener(e -> manageCourses());
        scoreMenu.add(manageCourseItem);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");

        JMenuItem statsItem = new JMenuItem("统计信息");
        statsItem.addActionListener(e -> showStatistics());
        helpMenu.add(statsItem);

        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        // 添加菜单到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(sortMenu);
        menuBar.add(scoreMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * 管理选中学生课程
     */
    private void manageCourses() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请先在表格中选择一个学生", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(this,
                    "未找到该学生: " + studentId, "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        CourseManagementDialog dialog = new CourseManagementDialog(this, studentManager, student);
        dialog.setVisible(true);
        refreshTable();
        updateStatus("已更新课程/GPA: " + student.getName());
    }
    
    /**
     * 导出全体学生按GPA排名（含所有课程列）
     */
    private void exportAllRanking() {
        if (studentManager.getStudentCount() == 0) {
            JOptionPane.showMessageDialog(this, "暂无学生数据", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择导出 XLSX 文件");
        chooser.setSelectedFile(new java.io.File("全部学生排名导出.xlsx"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new java.io.File(file.getParentFile(), file.getName() + ".xlsx");
        }
        boolean ok = service.ExcelExporter.exportAllRankingXlsx(studentManager, file);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Excel 导出成功:\n" + file.getAbsolutePath(), "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Excel 导出失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 刷新表格数据
     */
    private void refreshTable() {
        updateTable(studentManager.getAllStudents());
        updateStatus("共 " + studentManager.getStudentCount() + " 条记录");
    }

    /**
     * 更新表格显示
     */
    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            Object[] row = {
                student.getStudentId(),
                student.getName(),
                student.getGender(),
                student.getAge(),
                student.getMajor(),
                student.getClassNumber(),
                student.getPhoneNumber()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        List<Student> results;

        if (keyword.isEmpty()) {
            results = studentManager.getAllStudents();
        } else {
            switch (searchType) {
                case "学号":
                    Student student = studentManager.findStudentById(keyword);
                    results = student != null ? List.of(student) : List.of();
                    break;
                case "姓名":
                    results = studentManager.searchByName(keyword);
                    break;
                case "专业":
                    results = studentManager.searchByMajor(keyword);
                    break;
                case "班级":
                    results = studentManager.searchByClass(keyword);
                    break;
                default:
                    results = studentManager.search(keyword);
                    break;
            }
        }

        updateTable(results);
        updateStatus("找到 " + results.size() + " 条记录");
    }

    /**
     * 添加学生
     */
    private void addStudent() {
        StudentDialog dialog = new StudentDialog(this, "添加学生", null);
        dialog.setVisible(true);

        Student newStudent = dialog.getStudent();
        if (newStudent != null) {
            try {
                studentManager.addStudent(newStudent);
                refreshTable();
                updateStatus("成功添加学生: " + newStudent.getName());
                JOptionPane.showMessageDialog(this,
                        "学生添加成功!", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "添加失败: " + e.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 编辑学生
     */
    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请先选择要编辑的学生!", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = studentManager.findStudentById(studentId);

        if (student != null) {
            StudentDialog dialog = new StudentDialog(this, "编辑学生", student);
            dialog.setVisible(true);

            Student updatedStudent = dialog.getStudent();
            if (updatedStudent != null) {
                studentManager.updateStudent(updatedStudent);
                refreshTable();
                updateStatus("成功更新学生: " + updatedStudent.getName());
                JOptionPane.showMessageDialog(this,
                        "学生信息更新成功!", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * 删除学生
     */
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请先选择要删除的学生!", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(this,
                "确定要删除学生 " + name + " (" + studentId + ") 吗?",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            if (studentManager.deleteStudent(studentId)) {
                refreshTable();
                updateStatus("成功删除学生: " + name);
                JOptionPane.showMessageDialog(this,
                        "学生删除成功!", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "删除失败!", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 显示统计信息
     */
    private void showStatistics() {
        StatisticsDialog dialog = new StatisticsDialog(this, studentManager);
        dialog.setVisible(true);
    }

    /**
     * 备份数据
     */
    private void backupData() {
        if (studentManager.backupData()) {
            JOptionPane.showMessageDialog(this,
                    "数据备份成功!", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            updateStatus("数据已备份");
        } else {
            JOptionPane.showMessageDialog(this,
                    "数据备份失败!", "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 导入成绩
     */
    private void importScores() {
        ScoreImportDialog dialog = new ScoreImportDialog(this, studentManager);
        dialog.setVisible(true);
        // 导入后刷新表格
        refreshTable();
    }

    /**
     * 显示专业排名
     */
    private void showMajorRanking() {
        MajorRankingFrame rankingFrame = new MajorRankingFrame(studentManager);
        rankingFrame.setVisible(true);
    }

    /**
     * 显示关于对话框
     */
    private void showAbout() {
        String message = "学生信息管理系统 v1.0\n\n" +
                "功能特点:\n" +
                "• 学生信息的增删改查\n" +
                "• 多条件搜索\n" +
                "• 数据统计分析\n" +
                "• 文件数据持久化\n" +
                "• 数据备份功能\n\n" +
                "技术栈:\n" +
                "• Java GUI (Swing)\n" +
                "• 文件I/O\n" +
                "• Java集合框架\n" +
                "• 异常处理";

        JOptionPane.showMessageDialog(this,
                message,
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 更新状态栏
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}
