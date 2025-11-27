package gui;

import model.Course;
import model.Student;
import service.StudentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CourseManagementDialog extends JDialog {
    private final StudentManager studentManager;
    private final Student student;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JLabel gpaLabel;

    public CourseManagementDialog(Frame parent, StudentManager studentManager, Student student) {
        super(parent, "课程管理 - " + student.getName() + " (" + student.getStudentId() + ")", true);
        this.studentManager = studentManager;
        this.student = student;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(650, 400));
        refreshTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        JPanel topPanel = new JPanel(new BorderLayout());
        gpaLabel = new JLabel();
        gpaLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        topPanel.add(gpaLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"课程名称","学分","成绩"},0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("添加课程");
        addBtn.addActionListener(this::onAddCourse);
        JButton editBtn = new JButton("编辑课程");
        editBtn.addActionListener(this::onEditCourse);
        JButton delBtn = new JButton("删除课程");
        delBtn.addActionListener(this::onDeleteCourse);
        JButton clearBtn = new JButton("清空课程");
        clearBtn.addActionListener(this::onClearCourses);
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
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
