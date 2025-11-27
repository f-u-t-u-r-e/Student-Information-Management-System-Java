package service;

import model.Course;
import model.Student;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导出工具类
 * 生成 Excel 可打开的 CSV 文件（UTF-8 BOM）
 */
public class ExportUtil {

    /**
     * 导出某专业排名（按 GPA 降序，处理并列），包含所有出现过的课程成绩与加权均分。
     * @param studentManager 学生管理器
     * @param major 专业名称
     * @param targetFile 目标文件（建议 .csv 后缀）
     * @return 成功/失败
     */
    public static boolean exportMajorRanking(StudentManager studentManager, String major, File targetFile) {
        List<Student> students = studentManager.getStudentsByMajorRanked(major);
        if (students.isEmpty()) return false;
        return exportRankingInternal(students, major, targetFile);
    }

    /**
     * 导出全体学生按 GPA 排名
     */
    public static boolean exportAllRanking(StudentManager studentManager, File targetFile) {
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        students.sort((a,b) -> Double.compare(b.getGpa(), a.getGpa()));
        return exportRankingInternal(students, "全部学生", targetFile);
    }

    private static boolean exportRankingInternal(List<Student> students, String title, File targetFile) {
        // 收集所有课程名
        LinkedHashSet<String> courseNames = new LinkedHashSet<>();
        for (Student s : students) {
            if (s.getCourses() != null) {
                for (Course c : s.getCourses()) {
                    courseNames.add(c.getCourseName());
                }
            }
        }

        // 处理并列排名: GPA 相同使用同一排名值
        List<Student> sorted = new ArrayList<>(students);
        // 已排序传入：专业排名时已排序，全集排名这里也排序
        // 重新计算并列排名
        Map<String,Integer> rankMap = new HashMap<>(); // studentId -> rank
        double prevGpa = -1;
        int rank = 1;
        int actualRank = 1;
        for (Student s : sorted) {
            if (s.getGpa() != prevGpa) {
                actualRank = rank;
                prevGpa = s.getGpa();
            }
            rankMap.put(s.getStudentId(), actualRank);
            rank++;
        }

        // 准备写出
        ensureParent(targetFile);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {
            // 写 BOM 让 Excel 识别 UTF-8
            writer.write('\uFEFF');
            writer.write(title + "排名导出" + "\n");
            writer.write("导出时间," + new Date() + "\n\n");

            // 表头
            List<String> headers = new ArrayList<>();
            headers.add("排名");
            headers.add("学号");
            headers.add("姓名");
            headers.add("性别");
            headers.add("年龄");
            headers.add("专业");
            headers.add("班级");
            headers.add("联系电话");
            headers.add("总学分");
            headers.add("课程数");
            // 动态课程列（课程名）
            headers.addAll(courseNames);
            headers.add("加权平均分(GPA)");
            writer.write(toCsvRow(headers));

            // 数据行
            for (Student s : sorted) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(rankMap.get(s.getStudentId())));
                row.add(s.getStudentId());
                row.add(s.getName());
                row.add(s.getGender());
                row.add(String.valueOf(s.getAge()));
                row.add(s.getMajor());
                row.add(s.getClassNumber());
                row.add(s.getPhoneNumber());
                row.add(String.format(Locale.CHINA, "%.1f", s.getTotalCredits()));
                row.add(String.valueOf(s.getCourseCount()));
                // 课程成绩映射
                Map<String, Course> courseMap = s.getCourses() == null ? Collections.emptyMap() : s.getCourses().stream()
                        .collect(Collectors.toMap(Course::getCourseName, c -> c, (a,b)->a));
                for (String cname : courseNames) {
                    Course c = courseMap.get(cname);
                    if (c == null) {
                        row.add("");
                    } else {
                        row.add(String.format(Locale.CHINA, "%.1f", c.getScore()));
                    }
                }
                row.add(String.format(Locale.CHINA, "%.2f", s.getGpa()));
                writer.write(toCsvRow(row));
            }

            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String toCsvRow(List<String> cells) {
        return cells.stream().map(ExportUtil::escapeCsv).collect(Collectors.joining(",")) + "\n";
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n");
        String value = s.replace("\"", "\"\"");
        if (needQuote) {
            return '"' + value + '"';
        }
        return value;
    }

    private static void ensureParent(File f) {
        File p = f.getParentFile();
        if (p != null && !p.exists()) p.mkdirs();
    }
}
