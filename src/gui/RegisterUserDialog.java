package gui;

import model.User;
import service.UserManager;

import javax.swing.*;
import java.awt.*;

public class RegisterUserDialog extends JDialog {
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"TEACHER", "STUDENT"});
    private final UserManager userManager;
    private boolean created;

    public RegisterUserDialog(Frame owner, UserManager um) {
        super(owner, "注册用户(管理员)", true);
        this.userManager = um;
        setSize(380, 240);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("用户名"), gbc);
        gbc.gridx = 1; form.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("密码"), gbc);
        gbc.gridx = 1; form.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("角色"), gbc);
        gbc.gridx = 1; form.add(roleCombo, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("创建");
        JButton cancelBtn = new JButton("取消");
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okBtn);

        okBtn.addActionListener(e -> onCreate());
        cancelBtn.addActionListener(e -> { created = false; dispose(); });
    }

    private void onCreate() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        String r = ((String) roleCombo.getSelectedItem());
        User.Role role = "TEACHER".equals(r) ? User.Role.TEACHER : User.Role.STUDENT;
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userManager.userExists(u)) {
            JOptionPane.showMessageDialog(this, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean ok = userManager.createUser(u, p, role);
        if (ok) {
            JOptionPane.showMessageDialog(this, "创建成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            created = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "创建失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCreated() { return created; }
}
