package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生实体类
 * 实现Serializable接口以支持对象序列化
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;      // 学号
    private String name;           // 姓名
    private String gender;         // 性别
    private int age;              // 年龄
    private String major;         // 专业
    private String classNumber;   // 班级
    private String phoneNumber;   // 联系电话

    // 成绩相关字段
    private List<Course> courses;  // 课程列表
    private double totalCredits;   // 总学分
    private double gpa;            // 加权平均分(GPA)

    public Student() {
        this.courses = new ArrayList<>();
    }

    public Student(String studentId, String name, String gender, int age,
                   String major, String classNumber, String phoneNumber) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.major = major;
        this.classNumber = classNumber;
        this.phoneNumber = phoneNumber;
        this.courses = new ArrayList<>();
        calculateGPA();
    }

    /**
     * 计算加权平均分(GPA)
     */
    public void calculateGPA() {
        if (courses == null || courses.isEmpty()) {
            this.totalCredits = 0;
            this.gpa = 0;
            return;
        }

        double totalWeightedScore = 0;
        double totalCredits = 0;

        for (Course course : courses) {
            totalWeightedScore += course.getWeightedScore();
            totalCredits += course.getCredit();
        }

        this.totalCredits = totalCredits;
        this.gpa = totalCredits > 0 ? totalWeightedScore / totalCredits : 0;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        calculateGPA();
    }

    /**
     * 添加课程
     */
    public void addCourse(Course course) {
        if (this.courses == null) {
            this.courses = new ArrayList<>();
        }
        this.courses.add(course);
        calculateGPA();
    }

    /**
     * 删除课程
     */
    public void removeCourse(String courseName) {
        if (this.courses != null) {
            this.courses.removeIf(c -> c.getCourseName().equals(courseName));
            calculateGPA();
        }
    }

    /**
     * 清空所有课程
     */
    public void clearCourses() {
        if (this.courses != null) {
            this.courses.clear();
            calculateGPA();
        }
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public double getGpa() {
        return gpa;
    }

    /**
     * 获取课程数量
     */
    public int getCourseCount() {
        return courses != null ? courses.size() : 0;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", major='" + major + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    /**
     * 将学生对象转换为CSV格式的字符串
     * 格式: 学号,姓名,性别,年龄,专业,班级,联系电话,[课程1:学分:成绩|课程2:学分:成绩|...]
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(studentId).append(",")
          .append(name).append(",")
          .append(gender).append(",")
          .append(age).append(",")
          .append(major).append(",")
          .append(classNumber).append(",")
          .append(phoneNumber);

        // 添加课程信息
        if (courses != null && !courses.isEmpty()) {
            sb.append(",[");
            for (int i = 0; i < courses.size(); i++) {
                if (i > 0) sb.append("|");
                sb.append(courses.get(i).toCSV());
            }
            sb.append("]");
        } else {
            sb.append(",[]");
        }

        return sb.toString();
    }

    /**
     * 从CSV格式的字符串创建学生对象
     * 支持旧格式(7个字段)和新格式(8个字段,包含课程)
     */
    public static Student fromCSV(String csvLine) {
        // 处理课程信息中的逗号,先提取课程部分
        int courseStart = csvLine.indexOf(",[");
        String basicInfo;
        String courseInfo = null;

        if (courseStart != -1) {
            basicInfo = csvLine.substring(0, courseStart);
            int courseEnd = csvLine.indexOf("]", courseStart);
            if (courseEnd != -1) {
                courseInfo = csvLine.substring(courseStart + 2, courseEnd);
            }
        } else {
            basicInfo = csvLine;
        }

        String[] parts = basicInfo.split(",");

        if (parts.length < 7) {
            throw new IllegalArgumentException("无效的CSV格式: 至少需要7个字段");
        }

        Student student = new Student(
            parts[0].trim(),
            parts[1].trim(),
            parts[2].trim(),
            Integer.parseInt(parts[3].trim()),
            parts[4].trim(),
            parts[5].trim(),
            parts[6].trim()
        );

        // 解析课程信息
        if (courseInfo != null && !courseInfo.trim().isEmpty()) {
            String[] courseParts = courseInfo.split("\\|");
            for (String courseCsv : courseParts) {
                if (!courseCsv.trim().isEmpty()) {
                    try {
                        Course course = Course.fromCSV(courseCsv.trim());
                        student.addCourse(course);
                    } catch (Exception e) {
                        System.err.println("解析课程失败: " + courseCsv + " - " + e.getMessage());
                    }
                }
            }
        }

        return student;
    }
}
