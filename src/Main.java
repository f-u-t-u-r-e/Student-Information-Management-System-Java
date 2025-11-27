import gui.MainFrame;

import javax.swing.*;

/**
 * 学生信息管理系统 - 主程序入口
 *
 * @author Student
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // 设置系统外观
        try {
            // 使用系统默认外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("无法设置系统外观: " + e.getMessage());
        }

        // 在事件调度线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("启动系统失败: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "系统启动失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
