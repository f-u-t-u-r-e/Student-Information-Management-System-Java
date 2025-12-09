package gui;

import service.UserManager;

import javax.swing.*;
import java.awt.*;

public class AdminResetPasswordDialog extends JDialog {
    private final JTextField targetUser = new JTextField(16);
    private final JPasswordField newPwd = new JPasswordField(16);
    private final UserManager userManager;
    private boolean reset;

    public AdminResetPasswordDialog(Frame owner, UserManager um) {
        super(owner, "管理员重置密码", true);
        this.userManager = um;
        setSize(380, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("用户名"), gbc);
        gbc.gridx = 1; form.add(targetUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("新密码"), gbc);
        gbc.gridx = 1; form.add(newPwd, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("重置");
        JButton cancelBtn = new JButton("取消");
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okBtn);

        okBtn.addActionListener(e -> onReset());
        cancelBtn.addActionListener(e -> { reset = false; dispose(); });
    }

    private void onReset() {
        String u = targetUser.getText().trim();
        String n = new String(newPwd.getPassword());
        if (u.isEmpty() || n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和新密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = userManager.resetPassword(u, n);
        if (ok) {
            JOptionPane.showMessageDialog(this, "重置成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            reset = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户不存在或重置失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isReset() { return reset; }
}
