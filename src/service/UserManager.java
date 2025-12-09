package service;

import model.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理：加载/保存用户文件；认证；创建用户；修改/重置密码
 * 文件格式：username,password,role  其中 role 为 ADMIN/TEACHER/STUDENT
 */
public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private File storeFile;

    public UserManager(String usersFilePath) {
        this.storeFile = resolvePath(usersFilePath);
        load();
        ensureAdmin();
    }

    private File resolvePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            File fallback = new File("../" + path);
            if (fallback.exists()) file = fallback;
        }
        return file;
    }

    private void load() {
        users.clear();
        if (storeFile == null || !storeFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(storeFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                String u = parts[0].trim();
                String p = parts[1].trim();
                String r = parts[2].trim().toUpperCase();
                User.Role role;
                if ("ADMIN".equals(r)) role = User.Role.ADMIN;
                else if ("TEACHER".equals(r)) role = User.Role.TEACHER;
                else role = User.Role.STUDENT;
                users.put(u, new User(u, p, role));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            if (storeFile == null) return;
            // 确保目录存在
            File parent = storeFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(storeFile), "UTF-8"))) {
                pw.println("# username,password,role");
                for (User u : users.values()) {
                    pw.println(u.getUsername() + "," + u.getPassword() + "," + u.getRole().name());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureAdmin() {
        if (!users.containsKey("admin")) {
            users.put("admin", new User("admin", "admin123", User.Role.ADMIN));
            save();
        }
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) return null;
        return user.getPassword().equals(password) ? user : null;
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public boolean createUser(String username, String password, User.Role role) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return false;
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password, role));
        save();
        return true;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User u = users.get(username);
        if (u == null) return false;
        if (!u.getPassword().equals(oldPassword)) return false;
        u.setPassword(newPassword);
        save();
        return true;
    }

    public boolean resetPassword(String targetUsername, String newPassword) {
        User u = users.get(targetUsername);
        if (u == null) return false;
        u.setPassword(newPassword);
        save();
        return true;
    }
}
