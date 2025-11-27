package service;

import model.Course;
import model.Student;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生管理类
 * 负责学生数据的增删改查等业务逻辑
 */
public class StudentManager {
    private List<Student> students;
    private FileManager fileManager;

    public StudentManager(String dataFilePath) {
        this.fileManager = new FileManager(dataFilePath);
        this.students = new ArrayList<>();
        loadData();
    }

    /**
     * 从文件加载数据
     */
    public void loadData() {
        students = fileManager.loadStudents();
        System.out.println("成功加载 " + students.size() + " 条学生记录");
    }

    /**
     * 保存数据到文件
     * @return 是否保存成功
     */
    public boolean saveData() {
        return fileManager.saveStudents(students);
    }

    /**
     * 添加学生
     * @param student 学生对象
     * @return 是否添加成功
     * @throws IllegalArgumentException 如果学号已存在
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("学生对象不能为空");
        }

        if (findStudentById(student.getStudentId()) != null) {
            throw new IllegalArgumentException("学号 " + student.getStudentId() + " 已存在");
        }

        students.add(student);
        return saveData();
    }

    /**
     * 删除学生
     * @param studentId 学号
     * @return 是否删除成功
     */
    public boolean deleteStudent(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return false;
        }

        students.remove(student);
        return saveData();
    }

    /**
     * 更新学生信息
     * @param updatedStudent 更新后的学生对象
     * @return 是否更新成功
     */
    public boolean updateStudent(Student updatedStudent) {
        if (updatedStudent == null) {
            throw new IllegalArgumentException("学生对象不能为空");
        }

        Student existingStudent = findStudentById(updatedStudent.getStudentId());
        if (existingStudent == null) {
            return false;
        }

        int index = students.indexOf(existingStudent);
        students.set(index, updatedStudent);
        return saveData();
    }

    /**
     * 根据学号查找学生
     * @param studentId 学号
     * @return 学生对象,如果不存在返回null
     */
    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }

    /**
     * 根据姓名搜索学生
     * @param name 姓名关键字
     * @return 匹配的学生列表
     */
    public List<Student> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>(students);
        }

        return students.stream()
                .filter(s -> s.getName().contains(name))
                .collect(Collectors.toList());
    }

    /**
     * 根据专业搜索学生
     * @param major 专业名称
     * @return 匹配的学生列表
     */
    public List<Student> searchByMajor(String major) {
        if (major == null || major.trim().isEmpty()) {
            return new ArrayList<>(students);
        }

        return students.stream()
                .filter(s -> s.getMajor().contains(major))
                .collect(Collectors.toList());
    }

    /**
     * 根据班级搜索学生
     * @param classNumber 班级
     * @return 匹配的学生列表
     */
    public List<Student> searchByClass(String classNumber) {
        if (classNumber == null || classNumber.trim().isEmpty()) {
            return new ArrayList<>(students);
        }

        return students.stream()
                .filter(s -> s.getClassNumber().contains(classNumber))
                .collect(Collectors.toList());
    }

    /**
     * 综合搜索(学号、姓名、专业、班级)
     * @param keyword 搜索关键字
     * @return 匹配的学生列表
     */
    public List<Student> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(students);
        }

        return students.stream()
                .filter(s -> s.getStudentId().contains(keyword) ||
                           s.getName().contains(keyword) ||
                           s.getMajor().contains(keyword) ||
                           s.getClassNumber().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有学生
     * @return 学生列表副本
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * 获取学生总数
     * @return 学生数量
     */
    public int getStudentCount() {
        return students.size();
    }

    /**
     * 按学号排序
     */
    public void sortByStudentId() {
        students.sort(Comparator.comparing(Student::getStudentId));
    }

    /**
     * 按姓名排序
     */
    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName));
    }

    /**
     * 按年龄排序
     */
    public void sortByAge() {
        students.sort(Comparator.comparingInt(Student::getAge));
    }

    /**
     * 获取统计信息
     * @return 统计信息Map
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("总人数", students.size());

        if (students.isEmpty()) {
            return stats;
        }

        // 男女人数统计
        long maleCount = students.stream().filter(s -> "男".equals(s.getGender())).count();
        long femaleCount = students.stream().filter(s -> "女".equals(s.getGender())).count();
        stats.put("男生人数", maleCount);
        stats.put("女生人数", femaleCount);

        // 平均年龄
        double avgAge = students.stream().mapToInt(Student::getAge).average().orElse(0);
        stats.put("平均年龄", String.format("%.1f", avgAge));

        // 专业分布
        Map<String, Long> majorDistribution = students.stream()
                .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        stats.put("专业分布", majorDistribution);

        return stats;
    }

    /**
     * 数据备份
     * @return 是否备份成功
     */
    public boolean backupData() {
        return fileManager.backupData();
    }

    /**
     * 按GPA排序(降序)
     */
    public void sortByGPA() {
        students.sort((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()));
    }

    /**
     * 获取指定专业的学生列表(按GPA排序)
     * @param major 专业名称
     * @return 该专业学生列表,按GPA降序排列
     */
    public List<Student> getStudentsByMajorRanked(String major) {
        return students.stream()
                .filter(s -> s.getMajor().equals(major))
                .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有专业列表
     * @return 专业列表
     */
    public List<String> getAllMajors() {
        return students.stream()
                .map(Student::getMajor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取学生在专业内的排名
     * @param studentId 学号
     * @return 排名(1为第一名),如果学生不存在或没有成绩返回-1
     */
    public int getRankInMajor(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return -1;
        }

        List<Student> majorStudents = getStudentsByMajorRanked(student.getMajor());
        for (int i = 0; i < majorStudents.size(); i++) {
            if (majorStudents.get(i).getStudentId().equals(studentId)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * 导入成绩数据从CSV文件
     * @param filePath 成绩文件路径
     * @return 导入成功的记录数
     */
    public int importScoresFromFile(String filePath) {
        int successCount = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    // 格式: 学号,课程名,学分,成绩
                    String[] parts = line.split(",");
                    if (parts.length != 4) {
                        System.err.println("跳过无效行: " + line);
                        continue;
                    }

                    String studentId = parts[0].trim();
                    String courseName = parts[1].trim();
                    double credit = Double.parseDouble(parts[2].trim());
                    double score = Double.parseDouble(parts[3].trim());

                    Student student = findStudentById(studentId);
                    if (student != null) {
                        student.addCourse(new Course(courseName, credit, score));
                        successCount++;
                    } else {
                        System.err.println("学号不存在: " + studentId);
                    }
                } catch (Exception e) {
                    System.err.println("解析行失败: " + line + " - " + e.getMessage());
                }
            }

            if (successCount > 0) {
                saveData();
            }
        } catch (IOException e) {
            System.err.println("读取成绩文件失败: " + e.getMessage());
        }

        return successCount;
    }
}
