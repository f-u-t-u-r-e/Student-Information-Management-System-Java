package gui;

import model.Student;

import javax.swing.*;
import java.awt.*;

/**
 * 学生信息编辑对话框
 * 用于添加和编辑学生信息
 */
public class StudentDialog extends JDialog {
    private Student student;
    private boolean isEdit;

    // 输入组件
    private JTextField studentIdField;
    private JTextField nameField;
    private JComboBox<String> genderCombo;
    private JSpinner ageSpinner;
    private JTextField majorField;
    private JTextField classField;
    private JTextField phoneField;

    private boolean confirmed = false;

    public StudentDialog(Frame parent, String title, Student existingStudent) {
        super(parent, title, true);

        this.isEdit = (existingStudent != null);
        this.student = existingStudent;

        initComponents();
        if (isEdit) {
            fillData(existingStudent);
        }

        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 学号
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("学号:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        studentIdField = new JTextField(20);
        if (isEdit) {
            studentIdField.setEditable(false);
            studentIdField.setBackground(Color.LIGHT_GRAY);
        }
        mainPanel.add(studentIdField, gbc);

        row++;

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);

        row++;

        // 性别
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("性别:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        genderCombo = new JComboBox<>(new String[]{"男", "女"});
        mainPanel.add(genderCombo, gbc);

        row++;

        // 年龄
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("年龄:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        SpinnerNumberModel ageModel = new SpinnerNumberModel(20, 15, 100, 1);
        ageSpinner = new JSpinner(ageModel);
        mainPanel.add(ageSpinner, gbc);

        row++;

        // 专业
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("专业:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        majorField = new JTextField(20);
        mainPanel.add(majorField, gbc);

        row++;

        // 班级
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("班级:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        classField = new JTextField(20);
        mainPanel.add(classField, gbc);

        row++;

        // 联系电话
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("联系电话:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> onConfirm());
        buttonPanel.add(confirmButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> onCancel());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 设置默认按钮
        getRootPane().setDefaultButton(confirmButton);

        // ESC键取消
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().registerKeyboardAction(
                e -> onCancel(),
                escapeKeyStroke,
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * 填充已有数据
     */
    private void fillData(Student student) {
        studentIdField.setText(student.getStudentId());
        nameField.setText(student.getName());
        genderCombo.setSelectedItem(student.getGender());
        ageSpinner.setValue(student.getAge());
        majorField.setText(student.getMajor());
        classField.setText(student.getClassNumber());
        phoneField.setText(student.getPhoneNumber());
    }

    /**
     * 确定按钮处理
     */
    private void onConfirm() {
        try {
            // 验证输入
            if (!validateInput()) {
                return;
            }

            // 创建学生对象
            String studentId = studentIdField.getText().trim();
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            int age = (Integer) ageSpinner.getValue();
            String major = majorField.getText().trim();
            String classNumber = classField.getText().trim();
            String phone = phoneField.getText().trim();

            student = new Student(studentId, name, gender, age, major, classNumber, phone);
            confirmed = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "数据错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 取消按钮处理
     */
    private void onCancel() {
        student = null;
        confirmed = false;
        dispose();
    }

    /**
     * 验证输入
     */
    private boolean validateInput() {
        // 学号验证
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入学号!",
                    "验证失败",
                    JOptionPane.WARNING_MESSAGE);
            studentIdField.requestFocus();
            return false;
        }

        // 姓名验证
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入姓名!",
                    "验证失败",
                    JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // 专业验证
        String major = majorField.getText().trim();
        if (major.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入专业!",
                    "验证失败",
                    JOptionPane.WARNING_MESSAGE);
            majorField.requestFocus();
            return false;
        }

        // 班级验证
        String classNumber = classField.getText().trim();
        if (classNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入班级!",
                    "验证失败",
                    JOptionPane.WARNING_MESSAGE);
            classField.requestFocus();
            return false;
        }

        // 电话验证
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入联系电话!",
                    "验证失败",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        if (!phone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this,
                    "请输入11位数字的手机号码!",
                    "验证失败",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * 获取学生对象
     */
    public Student getStudent() {
        return confirmed ? student : null;
    }
}
