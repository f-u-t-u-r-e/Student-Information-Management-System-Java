package gui;

import model.Course;
import model.Student;
import service.StudentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CourseManagementDialog extends JDialog {
    private final StudentManager studentManager;
    private final Student student;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JLabel gpaLabel;

    // 配色方案
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(230, 126, 34);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color HEADER_COLOR = new Color(52, 73, 94);

    public CourseManagementDialog(Frame parent, StudentManager studentManager, Student student) {
        super(parent, "课程管理 - " + student.getName() + " (" + student.getStudentId() + ")", true);
        this.studentManager = studentManager;
        this.student = student;
        getContentPane().setBackground(BACKGROUND_COLOR);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(750, 500));
        refreshTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));

        // 标题栏面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel titleLabel = new JLabel("课程与成绩管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // GPA信息面板
        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        gpaPanel.setBackground(CARD_COLOR);
        gpaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        gpaLabel = new JLabel();
        gpaLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gpaLabel.setForeground(TEXT_COLOR);
        gpaPanel.add(gpaLabel);

        // 表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.add(gpaPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"课程名称","学分","成绩"},0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(32);
        courseTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        courseTable.setSelectionBackground(new Color(52, 152, 219, 100));
        courseTable.setSelectionForeground(TEXT_COLOR);
        courseTable.setGridColor(new Color(189, 195, 199));
        courseTable.setShowGrid(true);
        courseTable.setIntercellSpacing(new Dimension(1, 1));

        // 设置表头样式
        JTableHeader header = courseTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createLineBorder(HEADER_COLOR));

        // 居中对齐
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        btnPanel.setBackground(CARD_COLOR);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        JButton addBtn = createStyledButton("添加课程", ACCENT_COLOR);
        addBtn.addActionListener(this::onAddCourse);
        btnPanel.add(addBtn);

        JButton editBtn = createStyledButton("编辑课程", PRIMARY_COLOR);
        editBtn.addActionListener(this::onEditCourse);
        btnPanel.add(editBtn);

        JButton delBtn = createStyledButton("删除课程", DANGER_COLOR);
        delBtn.addActionListener(this::onDeleteCourse);
        btnPanel.add(delBtn);

        JButton clearBtn = createStyledButton("清空课程", WARNING_COLOR);
        clearBtn.addActionListener(this::onClearCourses);
        btnPanel.add(clearBtn);

        JButton closeBtn = createStyledButton("关闭", SECONDARY_COLOR);
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);
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
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 36));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));

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

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Course> courses = student.getCourses();
        for (Course c : courses) {
            tableModel.addRow(new Object[]{c.getCourseName(), c.getCredit(), c.getScore()});
        }
        student.calculateGPA();
        gpaLabel.setText("课程数: " + student.getCourseCount() + "  总学分: " + student.getTotalCredits() + "  GPA: " + String.format("%.2f", student.getGpa()));
    }

    private void persistAndRefresh() {
        student.calculateGPA();
        studentManager.saveData();
        refreshTable();
    }

    private void onAddCourse(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField creditField = new JTextField();
        JTextField scoreField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0,2,5,5));
        panel.add(new JLabel("课程名称:")); panel.add(nameField);
        panel.add(new JLabel("学分:")); panel.add(creditField);
        panel.add(new JLabel("成绩:")); panel.add(scoreField);
        int result = JOptionPane.showConfirmDialog(this, panel, "添加课程", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double credit = Double.parseDouble(creditField.getText().trim());
                double score = Double.parseDouble(scoreField.getText().trim());
                if (name.isEmpty()) throw new IllegalArgumentException("课程名称不能为空");
                student.addCourse(new Course(name, credit, score));
                persistAndRefresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "添加失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditCourse(ActionEvent e) {
        int row = courseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Course course = student.getCourses().get(row);
        JTextField nameField = new JTextField(course.getCourseName());
        JTextField creditField = new JTextField(String.valueOf(course.getCredit()));
        JTextField scoreField = new JTextField(String.valueOf(course.getScore()));
        JPanel panel = new JPanel(new GridLayout(0,2,5,5));
        panel.add(new JLabel("课程名称:")); panel.add(nameField);
        panel.add(new JLabel("学分:")); panel.add(creditField);
        panel.add(new JLabel("成绩:")); panel.add(scoreField);
        int result = JOptionPane.showConfirmDialog(this, panel, "编辑课程", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double credit = Double.parseDouble(creditField.getText().trim());
                double score = Double.parseDouble(scoreField.getText().trim());
                if (name.isEmpty()) throw new IllegalArgumentException("课程名称不能为空");
                course.setCourseName(name);
                course.setCredit(credit);
                course.setScore(score);
                persistAndRefresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "更新失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteCourse(ActionEvent e) {
        int row = courseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Course course = student.getCourses().get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "确定删除课程 " + course.getCourseName() + "?", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            student.removeCourse(course.getCourseName());
            persistAndRefresh();
        }
    }

    private void onClearCourses(ActionEvent e) {
        if (student.getCourseCount() == 0) return;
        int confirm = JOptionPane.showConfirmDialog(this, "确定清空该学生所有课程?", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            student.clearCourses();
            persistAndRefresh();
        }
    }
}
