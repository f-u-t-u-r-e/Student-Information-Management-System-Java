package gui;

import service.UserManager;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    private final JPasswordField oldPwd = new JPasswordField(16);
    private final JPasswordField newPwd = new JPasswordField(16);
    private final UserManager userManager;
    private final String username;
    private boolean changed;

    public ChangePasswordDialog(Frame owner, UserManager um, String username) {
        super(owner, "修改密码", true);
        this.userManager = um;
        this.username = username;
        setSize(360, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("原密码"), gbc);
        gbc.gridx = 1; form.add(oldPwd, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("新密码"), gbc);
        gbc.gridx = 1; form.add(newPwd, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("修改");
        JButton cancelBtn = new JButton("取消");
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okBtn);

        okBtn.addActionListener(e -> onChange());
        cancelBtn.addActionListener(e -> { changed = false; dispose(); });
    }

    private void onChange() {
        String o = new String(oldPwd.getPassword());
        String n = new String(newPwd.getPassword());
        if (n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "新密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = userManager.changePassword(username, o, n);
        if (ok) {
            JOptionPane.showMessageDialog(this, "修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            changed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "原密码错误或修改失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isChanged() { return changed; }
}
