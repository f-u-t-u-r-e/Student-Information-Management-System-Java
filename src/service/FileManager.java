package service;

import model.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理类
 * 负责学生数据的文件读写操作
 */
public class FileManager {
    private String filePath;

    public FileManager(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * 确保数据文件存在
     */
    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            // 创建父目录
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 创建文件
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("创建文件失败: " + e.getMessage());
        }
    }

    /**
     * 从文件中读取所有学生数据
     * @return 学生列表
     */
    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    try {
                        Student student = Student.fromCSV(line);
                        students.add(student);
                    } catch (Exception e) {
                        System.err.println("解析学生数据失败: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("文件不存在: " + filePath);
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    /**
     * 将学生数据保存到文件
     * @param students 学生列表
     * @return 是否保存成功
     */
    public boolean saveStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {

            // 写入注释说明
            writer.write("# 学生信息管理系统数据文件");
            writer.newLine();
            writer.write("# 格式: 学号,姓名,性别,年龄,专业,班级,联系电话,语文成绩,数学成绩,英语成绩");
            writer.newLine();

            // 写入学生数据
            for (Student student : students) {
                writer.write(student.toCSV());
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("保存文件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 备份数据文件
     * @return 是否备份成功
     */
    public boolean backupData() {
        try {
            File sourceFile = new File(filePath);
            if (!sourceFile.exists()) {
                return false;
            }

            String backupPath = filePath + ".backup";
            File backupFile = new File(backupPath);

            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(backupFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }

            return true;
        } catch (IOException e) {
            System.err.println("备份文件失败: " + e.getMessage());
            return false;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }
}
