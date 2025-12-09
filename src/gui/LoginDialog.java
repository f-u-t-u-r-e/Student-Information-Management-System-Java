package gui;

import model.User;
import service.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final UserManager userManager;
    private User loggedIn;

    public LoginDialog(Frame owner, UserManager um) {
        super(owner, "登录", true);
        this.userManager = um;
        setSize(360, 220);
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

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginBtn = new JButton("登录");
        JButton cancelBtn = new JButton("取消");
        buttons.add(cancelBtn);
        buttons.add(loginBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginBtn);

        JLabel hint = new JLabel("提示：初始管理员 admin/admin123");
        hint.setForeground(Color.GRAY);
        add(hint, BorderLayout.NORTH);

        loginBtn.addActionListener(e -> doLogin());
        cancelBtn.addActionListener(e -> {
            loggedIn = null;
            dispose();
        });
    }

    private void doLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        User user = userManager.authenticate(u, p);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.loggedIn = user;
        dispose();
    }

    public User getLoggedIn() { return loggedIn; }
}
