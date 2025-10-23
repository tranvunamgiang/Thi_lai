package com.example.thilai.repository;

import com.example.thilai.entity.Dish;
import com.example.thilai.repository.DishRepository;
import com.example.thilai.helper.MySqlHelper;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySqlDishRepository implements DishRepository {

    private static final Logger LOGGER = Logger.getLogger(MySqlDishRepository.class.getName());

    // Các câu lệnh SQL
    private final String FIND_ACTIVE_SQL =
            "SELECT d.*, c.category_name FROM Dish d JOIN Category c ON d.category_id = c.category_id WHERE d.status = 'DANG_BAN'";
    private final String FIND_BY_ID_SQL =
            "SELECT d.*, c.category_name FROM Dish d JOIN Category c ON d.category_id = c.category_id WHERE d.dish_id = ?";
    private final String INSERT_SQL =
            "INSERT INTO Dish (name, category_id, description, image_url, price, start_date, modified_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL =
            "UPDATE Dish SET name = ?, category_id = ?, description = ?, image_url = ?, price = ?, modified_date = ? " +
                    "WHERE dish_id = ?";
    private final String UPDATE_STATUS_SQL =
            "UPDATE Dish SET status = ?, modified_date = ? WHERE dish_id = ?";

    // Phương thức tiện ích để ánh xạ ResultSet sang Dish
    private Dish mapResultSetToDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setDishId(rs.getInt("dish_id"));
        dish.setName(rs.getString("name"));
        dish.setCategoryId(rs.getString("category_id"));
        dish.setCategoryName(rs.getString("category_name")); // Lấy từ bảng JOIN
        dish.setDescription(rs.getString("description"));
        dish.setImageUrl(rs.getString("image_url"));
        dish.setPrice(rs.getDouble("price"));

        // Chuyển đổi từ SQL DATE/DATETIME sang Java 8 Time API
        dish.setStartDate(rs.getDate("start_date"));
        dish.setModifiedDate(rs.getDate("modified_date"));

        dish.setStatus(rs.getString("status"));
        return dish;
    }

    // ------------------- READ -------------------

    @Override
    public List<Dish> findAllActiveDishes() {
        List<Dish> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = MySqlHelper.getInstance().getConnection();
            ps = conn.prepareStatement(FIND_ACTIVE_SQL);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToDish(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm món ăn đang bán.", e);
        } finally {
            MySqlHelper.closeResources(rs, ps, conn);
        }
        return list;
    }

    @Override
    public Optional<Dish> findById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Dish dish = null;

        try {
            conn = MySqlHelper.getInstance().getConnection();
            ps = conn.prepareStatement(FIND_BY_ID_SQL);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                dish = mapResultSetToDish(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm món ăn theo ID: " + id, e);
        } finally {
            MySqlHelper.closeResources(rs, ps, conn);
        }
        return Optional.ofNullable(dish);
    }

    // ------------------- CREATE -------------------

    @Override
    public boolean save(Dish dish) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            conn = MySqlHelper.getInstance().getConnection();
            ps = conn.prepareStatement(INSERT_SQL);

            // 1. name
            ps.setString(1, dish.getName());
            // 2. category_id
            ps.setString(2, dish.getCategoryId());
            // 3. description
            ps.setString(3, dish.getDescription());
            // 4. image_url
            ps.setString(4, dish.getImageUrl());
            // 5. price
            ps.setDouble(5, dish.getPrice());
            // 6. start_date (LocalDate -> Date)
            ps.setDate(6, dish.getStartDate());
            // 7. modified_date (LocalDateTime -> Timestamp)
            ps.setTimestamp(7, dish.getModifiedDate());
            // 8. status
            ps.setString(8, dish.getStatus());

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm món ăn mới.", e);
        } finally {
            MySqlHelper.closeResources(ps, conn);
        }
        return rowsAffected > 0;
    }

    // ------------------- UPDATE -------------------

    @Override
    public boolean update(Dish dish) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            conn = MySqlHelper.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_SQL);

            // 1. name
            ps.setString(1, dish.getName());
            // 2. category_id
            ps.setString(2, dish.getCategoryId());
            // 3. description
            ps.setString(3, dish.getDescription());
            // 4. image_url
            ps.setString(4, dish.getImageUrl());
            // 5. price
            ps.setDouble(5, dish.getPrice());
            // 6. modified_date
            ps.setTimestamp(6, Timestamp.valueOf(dish.getModifiedDate()));
            // WHERE dish_id
            ps.setInt(7, dish.getDishId());

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật món ăn ID: " + dish.getDishId(), e);
        } finally {
            MySqlHelper.closeResources(ps, conn);
        }
        return rowsAffected > 0;
    }

    @Override
    public boolean updateStatus(int dishId, String newStatus) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            conn = MySqlHelper.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_STATUS_SQL);

            // 1. status
            ps.setString(1, newStatus);
            // 2. modified_date (Cập nhật thời gian sửa khi thay đổi trạng thái)
            ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            // WHERE dish_id
            ps.setInt(3, dishId);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái món ăn ID: " + dishId, e);
        } finally {
            MySqlHelper.closeResources(ps, conn);
        }
        return rowsAffected > 0;
    }
}
