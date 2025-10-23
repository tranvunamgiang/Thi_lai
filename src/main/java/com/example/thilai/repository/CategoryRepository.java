package com.example.thilai.repository;

import com.example.thilai.entity.Category;
import java.util.List;

public interface CategoryRepository {
    /**
     * Lấy tất cả các danh mục món ăn hiện có.
     * @return Danh sách Category.
     */
    List<Category> findAll();

    /**
     * Tìm kiếm một danh mục theo Mã (ID).
     * @param id Mã danh mục.
     * @return Category hoặc null nếu không tìm thấy.
     */
    Category findById(String id);
}
