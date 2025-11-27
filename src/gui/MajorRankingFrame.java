package gui;

import model.Student;
import service.StudentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 专业排名窗口
 * 显示指定专业的学生成绩排名
 */
public class MajorRankingFrame extends JFrame {
    private StudentManager studentManager;
    private JComboBox<String> majorCombo;
    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    private final String[] columnNames = {
        "排名", "学号", "姓名", "班级", "总学分", "加权平均分(GPA)", "课程数"
    };

    // 配色方案
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color HEADER_COLOR = new Color(52, 73, 94);

    public MajorRankingFrame(StudentManager studentManager) {
        this.studentManager = studentManager;

        setTitle("专业成绩排名");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        initComponents();
        loadMajors();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));

        // 顶部选择面板
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(CARD_COLOR);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("选择专业:");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        leftPanel.add(titleLabel);

        majorCombo = new JComboBox<>();
        majorCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        majorCombo.setPreferredSize(new Dimension(280, 38));
        majorCombo.addActionListener(e -> loadRanking());
        leftPanel.add(majorCombo);

        topPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(CARD_COLOR);

        JButton refreshButton = createStyledButton("刷新数据", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> {
            loadMajors();
            loadRanking();
        });
        rightPanel.add(refreshButton);

        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 中间表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankingTable = new JTable(tableModel);
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.setRowHeight(32);
        rankingTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        rankingTable.setSelectionBackground(new Color(52, 152, 219, 100));
        rankingTable.setSelectionForeground(TEXT_COLOR);
        rankingTable.setGridColor(new Color(189, 195, 199));
        rankingTable.setShowGrid(true);
        rankingTable.setIntercellSpacing(new Dimension(1, 1));

        // 设置表头样式
        JTableHeader header = rankingTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        header.setBorder(BorderFactory.createLineBorder(HEADER_COLOR));

        // 设置列宽
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 排名
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 学号
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 姓名
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(120); // 班级
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 总学分
        rankingTable.getColumnModel().getColumn(5).setPreferredWidth(150); // GPA
        rankingTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 课程数

        // 居中对齐
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < rankingTable.getColumnCount(); i++) {
            rankingTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // 底部统计面板
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 0));
        bottomPanel.setBackground(HEADER_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        statsLabel = new JLabel("请选择专业");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statsLabel.setForeground(Color.WHITE);
        bottomPanel.add(statsLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        actionPanel.setBackground(HEADER_COLOR);
        JButton previewButton = createStyledButton("预览排名", WARNING_COLOR);
        previewButton.addActionListener(e -> previewRanking());
        JButton exportButton = createStyledButton("导出Excel", ACCENT_COLOR);
        exportButton.addActionListener(e -> exportRankingToExcel());
        actionPanel.add(previewButton);
        actionPanel.add(exportButton);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // 添加边距
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * 创建样式化按钮
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 30, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

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
     * 加载所有专业到下拉框
     */
    private void loadMajors() {
        majorCombo.removeAllItems();
        List<String> majors = studentManager.getAllMajors();

        if (majors.isEmpty()) {
            statsLabel.setText("暂无专业数据");
            return;
        }

        for (String major : majors) {
            majorCombo.addItem(major);
        }
    }

    /**
     * 加载选中专业的排名
     */
    private void loadRanking() {
        String selectedMajor = (String) majorCombo.getSelectedItem();
        if (selectedMajor == null) {
            return;
        }

        tableModel.setRowCount(0);
        List<Student> students = studentManager.getStudentsByMajorRanked(selectedMajor);

        if (students.isEmpty()) {
            statsLabel.setText("该专业暂无学生数据");
            return;
        }

        int rank = 1;
        double prevGpa = -1;
        int actualRank = 1;

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);

            // 处理并列排名
            if (student.getGpa() != prevGpa) {
                actualRank = rank;
                prevGpa = student.getGpa();
            }

            Object[] row = {
                actualRank,
                student.getStudentId(),
                student.getName(),
                student.getClassNumber(),
                String.format("%.1f", student.getTotalCredits()),
                String.format("%.2f", student.getGpa()),
                student.getCourseCount()
            };
            tableModel.addRow(row);
            rank++;
        }

        // 更新统计信息
        double avgGpa = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0);

        long withScores = students.stream()
                .filter(s -> s.getCourseCount() > 0)
                .count();

        statsLabel.setText(String.format(
            "专业: %s  |  总人数: %d  |  已录入成绩: %d  |  专业平均GPA: %.2f",
            selectedMajor, students.size(), withScores, avgGpa
        ));
    }

    /**
     * 导出排名数据
     */
    private void previewRanking() {
        String selectedMajor = (String) majorCombo.getSelectedItem();
        if (selectedMajor == null || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "没有可导出的数据!", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(selectedMajor).append(" 专业成绩排名\n");
        sb.append("=".repeat(80)).append("\n\n");

        // 表头
        for (String col : columnNames) {
            sb.append(String.format("%-15s", col));
        }
        sb.append("\n");
        sb.append("-".repeat(80)).append("\n");

        // 数据行
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < columnNames.length; j++) {
                sb.append(String.format("%-15s", tableModel.getValueAt(i, j)));
            }
            sb.append("\n");
        }

        sb.append("\n").append(statsLabel.getText());

        // 显示在对话框
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "排名数据预览",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 导出Excel (CSV UTF-8 BOM)
     */
    private void exportRankingToExcel() {
        String selectedMajor = (String) majorCombo.getSelectedItem();
        if (selectedMajor == null || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "没有可导出的数据!", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择导出 XLSX 文件位置");
        chooser.setSelectedFile(new java.io.File(selectedMajor + "_排名导出.xlsx"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        java.io.File file = chooser.getSelectedFile();
        // 如果用户没写后缀，补 .xlsx
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new java.io.File(file.getParentFile(), file.getName() + ".xlsx");
        }
        boolean ok = service.ExcelExporter.exportMajorRankingXlsx(studentManager, selectedMajor, file);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Excel 导出成功:\n" + file.getAbsolutePath(), "成功",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Excel 导出失败", "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
