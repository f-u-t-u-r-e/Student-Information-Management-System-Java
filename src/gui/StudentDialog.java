package gui;

import model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    // 配色方案
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

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
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));

        // 标题栏
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(isEdit ? "编辑学生信息" : "添加新学生");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 学号
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel studentIdLabel = new JLabel("学号:");
        studentIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        studentIdLabel.setForeground(TEXT_COLOR);
        mainPanel.add(studentIdLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        studentIdField = createStyledTextField();
        if (isEdit) {
            studentIdField.setEditable(false);
            studentIdField.setBackground(new Color(236, 240, 241));
        }
        mainPanel.add(studentIdField, gbc);

        row++;

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("姓名:");
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_COLOR);
        mainPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = createStyledTextField();
        mainPanel.add(nameField, gbc);

        row++;

        // 性别
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel genderLabel = new JLabel("性别:");
        genderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        genderLabel.setForeground(TEXT_COLOR);
        mainPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        genderCombo = new JComboBox<>(new String[]{"男", "女"});
        genderCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        genderCombo.setPreferredSize(new Dimension(300, 35));
        mainPanel.add(genderCombo, gbc);

        row++;

        // 年龄
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel ageLabel = new JLabel("年龄:");
        ageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ageLabel.setForeground(TEXT_COLOR);
        mainPanel.add(ageLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        SpinnerNumberModel ageModel = new SpinnerNumberModel(20, 15, 100, 1);
        ageSpinner = new JSpinner(ageModel);
        ageSpinner.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ageSpinner.setPreferredSize(new Dimension(300, 35));
        mainPanel.add(ageSpinner, gbc);

        row++;

        // 专业
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel majorLabel = new JLabel("专业:");
        majorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        majorLabel.setForeground(TEXT_COLOR);
        mainPanel.add(majorLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        majorField = createStyledTextField();
        mainPanel.add(majorField, gbc);

        row++;

        // 班级
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel classLabel = new JLabel("班级:");
        classLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        classLabel.setForeground(TEXT_COLOR);
        mainPanel.add(classLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        classField = createStyledTextField();
        mainPanel.add(classField, gbc);

        row++;

        // 联系电话
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel phoneLabel = new JLabel("联系电话:");
        phoneLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        phoneLabel.setForeground(TEXT_COLOR);
        mainPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        phoneField = createStyledTextField();
        mainPanel.add(phoneField, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JButton confirmButton = createStyledButton("确定", ACCENT_COLOR);
        confirmButton.addActionListener(e -> onConfirm());
        buttonPanel.add(confirmButton);

        JButton cancelButton = createStyledButton("取消", SECONDARY_COLOR);
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
     * 创建样式化文本框
     */
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(300, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
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
        button.setPreferredSize(new Dimension(100, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
