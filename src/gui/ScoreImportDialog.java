package gui;

import service.StudentManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * 成绩导入对话框
 * 支持从CSV文件导入学生成绩
 */
public class ScoreImportDialog extends JDialog {
    private StudentManager studentManager;
    private JTextField filePathField;
    private JLabel workDirLabel;
    private JTextArea logArea;
    private JButton selectFileButton;
    private JButton importButton;

    // 配色方案
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(230, 126, 34);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public ScoreImportDialog(Frame parent, StudentManager studentManager) {
        // 使用非阻塞模式，避免父窗口阻塞导致感觉"不可点击"
        super(parent, "成绩导入", false);
        this.studentManager = studentManager;

        getContentPane().setBackground(BACKGROUND_COLOR);
        initComponents();
        pack();
        setMinimumSize(new Dimension(700, 550));
        setLocationRelativeTo(parent);
        setResizable(true);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));

        // 标题栏
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel titleLabel = new JLabel("成绩数据导入");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // 主内容面板
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 说明面板
        JPanel instructionPanel = new JPanel(new BorderLayout(0, 10));
        instructionPanel.setBackground(CARD_COLOR);
        instructionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel instructionTitleLabel = new JLabel("文件格式说明");
        instructionTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        instructionTitleLabel.setForeground(TEXT_COLOR);
        instructionPanel.add(instructionTitleLabel, BorderLayout.NORTH);

        JTextArea instructionText = new JTextArea(
            "CSV文件,每行一条成绩记录\n" +
            "格式: 学号,课程名,学分,成绩\n\n" +
            "示例:\n" +
            "2021001,高等数学,4.0,85.5\n" +
            "2021001,大学英语,3.0,90.0\n" +
            "2021002,数据结构,4.0,88.0"
        );
        instructionText.setEditable(false);
        instructionText.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        instructionText.setForeground(new Color(127, 140, 141));
        instructionText.setBackground(new Color(250, 250, 250));
        instructionText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instructionPanel.add(instructionText, BorderLayout.CENTER);

        mainPanel.add(instructionPanel, BorderLayout.NORTH);

        // 文件选择面板
        JPanel filePanel = new JPanel(new BorderLayout(15, 0));
        filePanel.setBackground(CARD_COLOR);
        filePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel fileLabel = new JLabel("成绩文件:");
        fileLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        fileLabel.setForeground(TEXT_COLOR);
        filePanel.add(fileLabel, BorderLayout.WEST);

        filePathField = new JTextField();
        filePathField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        filePathField.setPreferredSize(new Dimension(300, 36));
        filePathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        filePanel.add(filePathField, BorderLayout.CENTER);

        selectFileButton = createStyledButton("选择文件", PRIMARY_COLOR);
        selectFileButton.addActionListener(e -> selectFile());
        filePanel.add(selectFileButton, BorderLayout.EAST);

        mainPanel.add(filePanel, BorderLayout.CENTER);

        // 日志面板
        JPanel logPanel = new JPanel(new BorderLayout(0, 10));
        logPanel.setBackground(CARD_COLOR);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel logHeaderPanel = new JPanel(new BorderLayout());
        logHeaderPanel.setBackground(CARD_COLOR);

        JLabel logLabel = new JLabel("导入日志:");
        logLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        logLabel.setForeground(TEXT_COLOR);
        logHeaderPanel.add(logLabel, BorderLayout.WEST);

        workDirLabel = new JLabel("工作目录: " + System.getProperty("user.dir"));
        workDirLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        workDirLabel.setForeground(new Color(127, 140, 141));
        logHeaderPanel.add(workDirLabel, BorderLayout.EAST);

        logPanel.add(logHeaderPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        // 使用支持中文的字体，避免中文字符显示为方块或乱码
        // 优先使用系统常见中文字体，若不可用则回退到默认字体
        Font cjkFont = new Font("微软雅黑", Font.PLAIN, 12);
        if (!cjkFont.canDisplay('中')) {
            cjkFont = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
        }
        if (!cjkFont.canDisplay('文')) {
            cjkFont = new Font("SimSun", Font.PLAIN, 12);
        }
        logArea.setFont(cjkFont);
        logArea.setForeground(TEXT_COLOR);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(600, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        logPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        importButton = createStyledButton("开始导入", ACCENT_COLOR);
        importButton.setEnabled(false);
        importButton.addActionListener(e -> importScores());
        buttonPanel.add(importButton);

        JButton closeButton = createStyledButton("关闭", SECONDARY_COLOR);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
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
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 30, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // 鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * 选择文件
     */
    private void selectFile() {
        // 使用绝对路径构建 data 目录，避免相对路径解析异常
        File initialDir = new File(System.getProperty("user.dir"), "data");
        if (!initialDir.exists()) {
            File fallback = new File(System.getProperty("user.dir"), "../data");
            if (fallback.exists()) {
                initialDir = fallback;
            }
        }

        // 确保在事件派发线程中执行文件选择器
        JFileChooser fileChooser = new JFileChooser(initialDir.getAbsolutePath());
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "CSV或文本文件 (*.csv, *.txt)", "csv", "txt"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filePathField.setText(file.getAbsolutePath());
            importButton.setEnabled(true);
            logArea.append("已选择文件: " + file.getAbsolutePath() + "\n");
            logArea.append("文件存在: " + file.exists() + "\n");
        } else {
            logArea.append("文件选择已取消或失败\n");
        }
    }

    /**
     * 导入成绩
     */
    private void importScores() {
        String filePath = filePathField.getText().trim();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先选择成绩文件!", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        File f = new File(filePath);
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this,
                    "文件不存在: " + filePath, "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!filePath.toLowerCase().endsWith(".csv") && !filePath.toLowerCase().endsWith(".txt")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "文件扩展名不是 .csv 或 .txt, 继续导入?", "确认",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        logArea.append("开始导入成绩数据...\n");
        importButton.setEnabled(false);
        selectFileButton.setEnabled(false);

        // 在后台线程执行导入
        SwingWorker<Integer, String> worker = new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return studentManager.importScoresFromFile(filePath);
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    logArea.append(message + "\n");
                }
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    logArea.append("导入完成!\n");
                    logArea.append("成功导入 " + count + " 条成绩记录\n");

                    JOptionPane.showMessageDialog(ScoreImportDialog.this,
                            "成功导入 " + count + " 条成绩记录!",
                            "导入成功",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    logArea.append("导入失败: " + e.getMessage() + "\n");
                    JOptionPane.showMessageDialog(ScoreImportDialog.this,
                            "导入失败: " + e.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    importButton.setEnabled(true);
                    selectFileButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
}
