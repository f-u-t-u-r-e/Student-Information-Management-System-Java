package gui;

import service.StudentManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatisticsDialog extends JDialog {
    private final StudentManager studentManager;

    public StatisticsDialog(Frame parent, StudentManager studentManager) {
        super(parent, "统计信息", true);
        this.studentManager = studentManager;
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        Map<String, Object> stats = studentManager.getStatistics();

        JPanel charts = new JPanel(new GridLayout(1,2,10,10));
        charts.add(new GenderPieChartPanel(
                (Long) stats.get("男生人数"),
                (Long) stats.get("女生人数")
        ));

        @SuppressWarnings("unchecked")
        Map<String, Long> majorDist = (Map<String, Long>) stats.get("专业分布");
        MajorBarChartPanel majorPanel = new MajorBarChartPanel(majorDist);
        // 当专业很多时，允许水平滚动
        JScrollPane majorScroll = new JScrollPane(majorPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        majorScroll.getHorizontalScrollBar().setUnitIncrement(24);
        charts.add(majorScroll);

        add(charts, BorderLayout.CENTER);

        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        summary.append("总人数: " + stats.get("总人数") + "\n");
        summary.append("男生人数: " + stats.get("男生人数") + "\n");
        summary.append("女生人数: " + stats.get("女生人数") + "\n");
        summary.append("平均年龄: " + stats.get("平均年龄") + "\n");
        add(new JScrollPane(summary), BorderLayout.SOUTH);
    }

    static class GenderPieChartPanel extends JPanel {
        private final long male;
        private final long female;

        GenderPieChartPanel(long male, long female) {
            this.male = male;
            this.female = female;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 80;
            int x = (w - size) / 2;
            int y = (h - size) / 2;

            double total = Math.max(1, male + female);
            double maleAngle = 360.0 * (male / total);

            g2.setColor(new Color(52, 152, 219));
            g2.fillArc(x, y, size, size, 0, (int) Math.round(maleAngle));
            g2.setColor(new Color(231, 76, 60));
            g2.fillArc(x, y, size, size, (int) Math.round(maleAngle), 360 - (int) Math.round(maleAngle));

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 16));
            g2.drawString("性别分布", 20, 30);
            g2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
            g2.setColor(new Color(52, 152, 219));
            g2.fillRect(20, 45, 14, 14);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("男: " + male, 40, 57);
            g2.setColor(new Color(231, 76, 60));
            g2.fillRect(120, 45, 14, 14);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("女: " + female, 140, 57);
        }
    }

    static class MajorBarChartPanel extends JPanel {
        private final Map<String, Long> data;

        MajorBarChartPanel(Map<String, Long> data) {
            this.data = data;
            setBackground(Color.WHITE);
            // 初始宽度按数据量扩展，便于水平滚动
            int baseWidth = 700;
            int perBar = 90; // 每个柱预留的宽度（含间隔）
            int barCount = data != null ? data.size() : 0;
            int preferredW = Math.max(baseWidth, barCount * perBar);
            setPreferredSize(new Dimension(preferredW, 360));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(Color.GRAY);
                g.drawString("无专业分布数据", 20, 20);
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int leftPadding = 60;
            int rightPadding = 40;
            int topPadding = 60;
            int bottomPadding = 110; // 给多行标签留更大空间
            int chartW = w - leftPadding - rightPadding;
            int chartH = h - topPadding - bottomPadding;

            long max = 1;
            for (long v : data.values()) max = Math.max(max, v);

            int count = data.size();
            int gap = 24;
            int barW = Math.max(20, chartW / Math.max(1, count) - gap);
            int i = 0;

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 18));
            g2.drawString("专业分布", leftPadding, topPadding - 20);

            // 画坐标轴与水平网格线
            g2.setColor(new Color(200, 200, 200));
            int axisX = leftPadding;
            int axisY = topPadding + chartH;
            g2.drawLine(axisX, topPadding, axisX, axisY); // y 轴
            g2.drawLine(axisX, axisY, axisX + chartW, axisY); // x 轴

            // 水平网格线（4条等分）
            g2.setColor(new Color(225, 225, 225));
            int gridLines = 4;
            for (int gl = 1; gl <= gridLines; gl++) {
                int gy = topPadding + (int) Math.round(chartH * (gl / (double) gridLines));
                g2.drawLine(axisX, gy, axisX + chartW, gy);
            }

            // x 轴刻度（每个柱底）
            g2.setColor(new Color(200, 200, 200));

            for (Map.Entry<String, Long> e : data.entrySet()) {
                int x = leftPadding + i * (barW + gap) + gap / 2;
                int barH = (int) Math.round((e.getValue() / (double) max) * chartH);
                int y = topPadding + (chartH - barH);

                g2.setColor(new Color(46, 204, 113));
                g2.fillRect(x, y, barW, barH);
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                // 数值标签居中显示在柱顶上方
                FontMetrics fm = g2.getFontMetrics();
                String valText = String.valueOf(e.getValue());
                int valWidth = fm.stringWidth(valText);
                g2.drawString(valText, x + (barW - valWidth) / 2, y - 8);

                // 专业标签：仅换行与字体缩放，不旋转
                String label = e.getKey();
                int labelFontSize = 12;
                Font labelFont = new Font("Microsoft YaHei UI", Font.PLAIN, labelFontSize);
                FontMetrics labelFm = g2.getFontMetrics(labelFont);
                int maxLabelPx = barW + 60;

                // 优先分行：按空格/顿号/逗号拆分为两行
                String[] parts = label.split("[\\s、，,]+");
                String line1 = label;
                String line2 = null;
                if (parts.length > 1) {
                    int mid = (int) Math.ceil(parts.length / 2.0);
                    line1 = String.join(" ", java.util.Arrays.copyOfRange(parts, 0, mid));
                    line2 = String.join(" ", java.util.Arrays.copyOfRange(parts, mid, parts.length));
                }

                // 动态缩放字体，保证每行不超宽
                while (labelFm.stringWidth(line1) > maxLabelPx || (line2 != null && labelFm.stringWidth(line2) > maxLabelPx)) {
                    if (labelFontSize <= 9) break;
                    labelFontSize--;
                    labelFont = labelFont.deriveFont((float) labelFontSize);
                    labelFm = g2.getFontMetrics(labelFont);
                }

                // 如果仍过宽，执行截断（分别截断两行）
                if (labelFm.stringWidth(line1) > maxLabelPx) {
                    line1 = truncateToWidth(line1, labelFm, maxLabelPx);
                }
                if (line2 != null && labelFm.stringWidth(line2) > maxLabelPx) {
                    line2 = truncateToWidth(line2, labelFm, maxLabelPx);
                }

                int labelCenterX = x + barW / 2;
                int baselineY = axisY + 18;
                g2.setColor(new Color(80, 80, 80));
                g2.setFont(labelFont);
                // 绘制第一行
                int w1 = labelFm.stringWidth(line1);
                g2.drawString(line1, labelCenterX - w1 / 2, baselineY + labelFm.getAscent());
                // 绘制第二行（如果有）
                if (line2 != null && !line2.isEmpty()) {
                    int w2 = labelFm.stringWidth(line2);
                    int lineH = labelFm.getHeight();
                    g2.drawString(line2, labelCenterX - w2 / 2, baselineY + labelFm.getAscent() + lineH);
                }

                // x 轴刻度线（短线）
                g2.setColor(new Color(180, 180, 180));
                g2.drawLine(labelCenterX, axisY, labelCenterX, axisY + 6);
                i++;
            }
        }

        private String truncateToWidth(String text, FontMetrics fm, int maxPx) {
            String ellipsis = "…";
            if (fm.stringWidth(text) <= maxPx) return text;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                String cand = sb.toString() + text.charAt(i);
                if (fm.stringWidth(cand + ellipsis) > maxPx) break;
                sb.append(text.charAt(i));
            }
            return sb.append(ellipsis).toString();
        }
    }
}
