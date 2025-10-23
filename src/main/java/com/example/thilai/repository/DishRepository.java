package com.example.thilai.repository;

import com.example.thilai.entity.Dish;
import java.util.List;
import java.util.Optional;

public interface DishRepository {
    // READ Operations
    /**
     * Lấy danh sách tất cả các món ăn đang bán ('DANG_BAN').
     * @return Danh sách Dish.
     */
    List<Dish> findAllActiveDishes();

    /**
     * Tìm kiếm một món ăn theo ID.
     * @param id Mã món ăn.
     * @return Optional<Dish> để xử lý trường hợp không tìm thấy an toàn.
     */
    Optional<Dish> findById(int id);

    // CREATE Operations
    /**
     * Thêm một món ăn mới vào DB.
     * @param dish Đối tượng Dish cần thêm.
     * @return true nếu thành công, false nếu thất bại.
     */
    boolean save(Dish dish);

    // UPDATE Operations
    /**
     * Cập nhật thông tin chi tiết của một món ăn đã tồn tại.
     * @param dish Đối tượng Dish với thông tin mới (cần có dishId).
     * @return true nếu thành công, false nếu thất bại.
     */
    boolean update(Dish dish);

    /**
     * Cập nhật trạng thái của món ăn (ví dụ: DUNG_BAN, DA_XOA).
     * @param dishId ID món ăn.
     * @param newStatus Trạng thái mới.
     * @return true nếu thành công, false nếu thất bại.
     */
    boolean updateStatus(int dishId, String newStatus);
}
