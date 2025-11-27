package gui;

import model.Student;
import service.StudentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    public MajorRankingFrame(StudentManager studentManager) {
        this.studentManager = studentManager;

        setTitle("专业成绩排名");
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadMajors();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 顶部选择面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("选择专业:");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        topPanel.add(titleLabel);

        majorCombo = new JComboBox<>();
        majorCombo.setPreferredSize(new Dimension(250, 30));
        majorCombo.addActionListener(e -> loadRanking());
        topPanel.add(majorCombo);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> {
            loadMajors();
            loadRanking();
        });
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // 中间表格面板
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankingTable = new JTable(tableModel);
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.setRowHeight(28);
        rankingTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));

        // 设置列宽
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 排名
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 学号
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 姓名
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(120); // 班级
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 总学分
        rankingTable.getColumnModel().getColumn(5).setPreferredWidth(150); // GPA
        rankingTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 课程数

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部统计面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        statsLabel = new JLabel("请选择专业");
        statsLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        bottomPanel.add(statsLabel, BorderLayout.WEST);

        JButton exportButton = new JButton("导出排名");
        exportButton.addActionListener(e -> exportRanking());
        bottomPanel.add(exportButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
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
    private void exportRanking() {
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
                "排名数据",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
