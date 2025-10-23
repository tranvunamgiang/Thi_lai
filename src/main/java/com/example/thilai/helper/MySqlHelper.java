package com.example.thilai.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Tên MySqlHelper cũng được, nhưng DBContext phổ biến hơn trong MVC
public class MySqlHelper {

    // Thông tin kết nối - NÊN được đặt trong JNDI hoặc file properties
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Vui lòng thay đổi
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static MySqlHelper instance;
    private static final Logger LOGGER = Logger.getLogger(MySqlHelper.class.getName());

    // 1. Private Constructor (để áp dụng Singleton Pattern)
    private MySqlHelper() {
        try {
            // Đăng ký JDBC Driver (bắt buộc trước Java 6, nhưng vẫn là thói quen tốt)
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found!", e);
        }
    }

    // 2. Phương thức getInstance (để lấy thể hiện duy nhất của lớp)
    public static synchronized MySqlHelper getInstance() {
        if (instance == null) {
            instance = new MySqlHelper();
        }
        return instance;
    }

    // 3. Phương thức lấy Connection
    /**
     * Lấy đối tượng Connection đến cơ sở dữ liệu.
     * @return Connection object hoặc null nếu kết nối thất bại.
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Có thể bỏ qua log này khi deploy
            // System.out.println("Kết nối DB thành công!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Không thể kết nối đến cơ sở dữ liệu!", e);
            System.err.println("Lỗi kết nối DB: " + e.getMessage());
        }
        return connection;
    }

    // 4. Phương thức đóng tài nguyên (Rất quan trọng)
    /**
     * Phương thức tiện ích để đóng các tài nguyên JDBC (Connection, Statement, ResultSet).
     * @param resources Các tài nguyên cần đóng (ResultSet, Statement, Connection).
     */
    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Lỗi khi đóng tài nguyên JDBC", e);
                }
            }
        }
    }
}
