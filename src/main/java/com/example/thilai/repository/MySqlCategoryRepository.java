package com.example.thilai.repository;

import com.example.thilai.entity.Category;
import com.example.thilai.repository.CategoryRepository;
import com.example.thilai.helper.MySqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySqlCategoryRepository implements CategoryRepository {

    private static final Logger LOGGER = Logger.getLogger(MySqlCategoryRepository.class.getName());

    private final String FIND_ALL_SQL = "SELECT category_id, category_name FROM Category";
    private final String FIND_BY_ID_SQL = "SELECT category_id, category_name FROM Category WHERE category_id = ?";

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = MySqlHelper.getInstance().getConnection();
            ps = conn.prepareStatement(FIND_ALL_SQL);
            rs = ps.executeQuery();

            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getString("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm tất cả danh mục.", e);
        } finally {
            MySqlHelper.closeResources(rs, ps, conn);
        }
        return categories;
    }

    @Override
    public Category findById(String id) {
        // Triển khai tương tự findAll, sử dụng PreparedStatement.setString(1, id) và trả về 1 đối tượng
        // (Không triển khai chi tiết ở đây để giữ mã ngắn gọn)
        return null;
    }
}
