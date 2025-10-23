package com.example.thilai.controller;

import com.example.thilai.entity.Dish;
import com.example.thilai.entity.Category;
import com.example.thilai.repository.DishRepository;
import com.example.thilai.repository.CategoryRepository;
// Chắc chắn các package này khớp với cấu trúc thư mục của bạn
import com.example.thilai.repository.MySqlDishRepository;
import com.example.thilai.repository.MySqlCategoryRepository;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
// Đã xóa import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Bỏ annotation @WebServlet vì chúng ta dùng web.xml
public class DishController extends HttpServlet {

    private DishRepository dishRepository;
    private CategoryRepository categoryRepository;
    private Instant Date;

    @Override
    public void init() throws ServletException {
        // Khởi tạo Repository implementations
        // Đảm bảo MySql...Repository là class, không phải interface
        this.dishRepository = new MySqlDishRepository();
        this.categoryRepository = new MySqlCategoryRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Thiết lập mã hóa UTF-8 cho response
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action") == null
                ? "list" : request.getParameter("action");

        try {
            switch (action) {
                case "create_form":
                    showCreateForm(request, response);
                    break;
                case "edit_form":
                    showEditForm(request, response);
                    break;
                case "stop_sale":
                    updateStatus(request, response, "DUNG_BAN");
                    break;
                case "delete":
                    updateStatus(request, response, "DA_XOA");
                    break;
                case "list":
                default:
                    listDishes(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Đảm bảo /error.jsp tồn tại
            request.setAttribute("error", "Lỗi xử lý hệ thống (GET): " + e.getMessage());
//            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Thiết lập mã hóa UTF-8 để nhận dữ liệu tiếng Việt từ form
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                createDish(request, response);
            } else if ("edit".equals(action)) {
                updateDish(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/dish?action=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Đảm bảo /error.jsp tồn tại
            request.setAttribute("error", "Lỗi xử lý hệ thống (POST): " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

// --- PRIVATE METHODS ---

    private void listDishes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Dish> dishList = dishRepository.findAllActiveDishes();
        request.setAttribute("dishList", dishList);
        // Kiểm tra đường dẫn JSP này (phải nằm trong webapp/)
        request.getRequestDispatcher("/dish_list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categoryList = categoryRepository.findAll();
        request.setAttribute("categoryList", categoryList);
        // Kiểm tra đường dẫn JSP này (phải nằm trong webapp/)
        request.getRequestDispatcher("/dish_form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dish?action=list");
            return;
        }

        int dishId = Integer.parseInt(idParam);
        Optional<Dish> dishOptional = dishRepository.findById(dishId);

        if (dishOptional.isPresent()) {
            List<Category> categoryList = categoryRepository.findAll();
            request.setAttribute("dish", dishOptional.get());
            request.setAttribute("categoryList", categoryList);
            request.getRequestDispatcher("/dish_form.jsp").forward(request, response);
        } else {
            // Không tìm thấy, chuyển hướng về trang danh sách
            response.sendRedirect(request.getContextPath() + "/dish?action=list");
        }
    }

    // --- VALIDATION & CREATE/UPDATE LOGIC ---

    private boolean validateAndPopulateDish(HttpServletRequest request, Dish dish, List<String> errors) {
        String name = request.getParameter("name");
        String categoryId = request.getParameter("categoryId");
        String priceStr = request.getParameter("price");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");

        // 1. Tên món ăn: Không trống và dài hơn 7 ký tự
        if (name == null || name.trim().isEmpty() || name.trim().length() < 8) {
            errors.add("Tên món ăn không được trống và phải dài hơn 7 ký tự.");
        }
        dish.setName(name);

        // 2. Danh mục: Không trống
        if (categoryId == null || categoryId.isEmpty()) {
            errors.add("Vui lòng chọn danh mục món ăn.");
        }
        dish.setCategoryId(categoryId);

        // 3. Giá bán: Phải lớn hơn 0
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                errors.add("Giá bán phải lớn hơn 0.");
            }
            dish.setPrice(price);
        } catch (NumberFormatException e) {
            errors.add("Giá bán không hợp lệ.");
        }

        // 4. Các trường còn lại
        dish.setDescription(description);
        dish.setImageUrl(imageUrl);

        return errors.isEmpty();
    }

    private void createDish(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> errors = new ArrayList<>();
        Dish dish = new Dish();

        if (validateAndPopulateDish(request, dish, errors)) {
            // Ngày bắt đầu bán là ngày hiện tại
            dish.setStartDate(java.sql.Date.valueOf(LocalDate.now()));
            // Ngày sửa thông tin là thời điểm hiện tại
            dish.setModifiedDate(java.sql.Date.valueOf(LocalDate.now()));
            // Trạng thái mặc định là Đang Bán
            dish.setStatus("DANG_BAN");

            if (dishRepository.save(dish)) {
                response.sendRedirect(request.getContextPath() + "/dish?action=list");
            } else {
                errors.add("Không thể thêm món ăn vào cơ sở dữ liệu. Vui lòng thử lại.");
                handleValidationFailure(request, response, errors, dish);
            }
        } else {
            // Validation thất bại
            handleValidationFailure(request, response, errors, dish);
        }
    }

    private void updateDish(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> errors = new ArrayList<>();
        int dishId;
        try {
            dishId = Integer.parseInt(request.getParameter("dishId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/dish?action=list");
            return;
        }

        Dish dish = new Dish();
        dish.setDishId(dishId);

        // Lấy lại Ngày bắt đầu bán (startDate) từ DB trước khi cập nhật
        dishRepository.findById(dishId).ifPresent(oldDish -> dish.setStartDate(oldDish.getStartDate()));

        if (validateAndPopulateDish(request, dish, errors)) {
            // Ngày sửa thông tin là thời điểm hiện tại
            dish.setModifiedDate(java.sql.Date.valueOf(LocalDate.now()));

            // Giữ nguyên trạng thái cũ (Trạng thái được cập nhật qua hàm updateStatus riêng)
            dishRepository.findById(dishId).ifPresent(oldDish -> dish.setStatus(oldDish.getStatus()));

            if (dishRepository.update(dish)) {
                response.sendRedirect(request.getContextPath() + "/dish?action=list");
            } else {
                errors.add("Không thể cập nhật món ăn vào cơ sở dữ liệu. Vui lòng thử lại.");
                handleValidationFailure(request, response, errors, dish);
            }
        } else {
            // Validation thất bại
            handleValidationFailure(request, response, errors, dish);
        }
    }

    private void handleValidationFailure(HttpServletRequest request, HttpServletResponse response, List<String> errors, Dish dish)
            throws ServletException, IOException {

        // Load lại danh mục để điền vào dropdown khi form load lại
        List<Category> categoryList = categoryRepository.findAll();
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("errors", errors);
        request.setAttribute("dish", dish); // Trả lại dữ liệu người dùng đã nhập
        request.getRequestDispatcher("/dish_form.jsp").forward(request, response);
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response, String status)
            throws IOException {

        try {
            int dishId = Integer.parseInt(request.getParameter("id"));
            dishRepository.updateStatus(dishId, status);
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("Lỗi khi thay đổi trạng thái món ăn: " + e.getMessage());
        }
        // Luôn chuyển hướng về trang danh sách sau khi thay đổi trạng thái
        response.sendRedirect(request.getContextPath() + "/dish?action=list");
    }
}