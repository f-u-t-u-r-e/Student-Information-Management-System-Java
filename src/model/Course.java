package model;

import java.io.Serializable;

/**
 * 课程成绩类
 * 包含课程名称、学分和成绩
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseName;  // 课程名称
    private double credit;      // 学分
    private double score;       // 成绩

    public Course() {
    }

    public Course(String courseName, double credit, double score) {
        this.courseName = courseName;
        this.credit = credit;
        this.score = score;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * 计算该课程的加权分数(成绩*学分)
     */
    public double getWeightedScore() {
        return score * credit;
    }

    /**
     * 转换为字符串格式: 课程名:学分:成绩
     */
    public String toCSV() {
        return courseName + ":" + credit + ":" + score;
    }

    /**
     * 从字符串解析: 课程名:学分:成绩
     */
    public static Course fromCSV(String csv) {
        String[] parts = csv.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("无效的课程格式: " + csv);
        }
        return new Course(
            parts[0].trim(),
            Double.parseDouble(parts[1].trim()),
            Double.parseDouble(parts[2].trim())
        );
    }

    @Override
    public String toString() {
        return courseName + "(学分:" + credit + ", 成绩:" + score + ")";
    }
}
