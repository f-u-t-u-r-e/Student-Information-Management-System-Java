package gui;

import service.StudentManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * 成绩导入对话框
 * 支持从CSV文件导入学生成绩
 */
public class ScoreImportDialog extends JDialog {
    private StudentManager studentManager;
    private JTextField filePathField;
    private JTextArea logArea;
    private JButton selectFileButton;
    private JButton importButton;

    public ScoreImportDialog(Frame parent, StudentManager studentManager) {
        super(parent, "成绩导入", true);
        this.studentManager = studentManager;

        initComponents();

        setSize(600, 450);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 顶部说明面板
        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("成绩数据导入");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        instructionPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea instructionText = new JTextArea(
            "文件格式说明:\n" +
            "CSV文件,每行一条成绩记录\n" +
            "格式: 学号,课程名,学分,成绩\n\n" +
            "示例:\n" +
            "2021001,高等数学,4.0,85.5\n" +
            "2021001,大学英语,3.0,90.0\n" +
            "2021002,数据结构,4.0,88.0"
        );
        instructionText.setEditable(false);
        instructionText.setBackground(new Color(245, 245, 245));
        instructionText.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        instructionPanel.add(instructionText, BorderLayout.CENTER);

        add(instructionPanel, BorderLayout.NORTH);

        // 中间文件选择面板
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        filePanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        JLabel fileLabel = new JLabel("成绩文件:");
        filePanel.add(fileLabel, BorderLayout.WEST);

        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePanel.add(filePathField, BorderLayout.CENTER);

        selectFileButton = new JButton("选择文件");
        selectFileButton.addActionListener(e -> selectFile());
        filePanel.add(selectFileButton, BorderLayout.EAST);

        add(filePanel, BorderLayout.CENTER);

        // 日志面板
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        JLabel logLabel = new JLabel("导入日志:");
        logPanel.add(logLabel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        logPanel.add(scrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.SOUTH);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        importButton = new JButton("开始导入");
        importButton.setEnabled(false);
        importButton.addActionListener(e -> importScores());
        buttonPanel.add(importButton);

        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // 将按钮面板添加到日志面板的底部
        logPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 选择文件
     */
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "CSV文件 (*.csv, *.txt)", "csv", "txt"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filePathField.setText(file.getAbsolutePath());
            importButton.setEnabled(true);
            logArea.setText("已选择文件: " + file.getName() + "\n");
        }
    }

    /**
     * 导入成绩
     */
    private void importScores() {
        String filePath = filePathField.getText();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先选择成绩文件!", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
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
