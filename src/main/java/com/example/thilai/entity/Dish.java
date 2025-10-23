package com.example.thilai.entity;

import java.util.Date;

public class Dish {
    private int dishId;
    private String name;
    private String categoryId;      // Mã danh mục (FK)
    private String categoryName;    // Tên danh mục (Dùng để hiển thị trong View)
    private String description;
    private String imageUrl;
    private double price;
    private Date startDate;    // Ngày bắt đầu bán (DATE)
    private Date modifiedDate; // Ngày sửa thông tin (DATETIME)
    private String status;          // Trạng thái: DANG_BAN, DUNG_BAN, DA_XOA

    // Constructors
    public Dish() {
    }

    public Dish(int dishId, String name, String categoryId, String description, String imageUrl, double price, Date startDate, Date modifiedDate, String status) {
        this.dishId = dishId;
        this.name = name;
        this.categoryId = categoryId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.startDate = startDate;
        this.modifiedDate = modifiedDate;
        this.status = status;
    }

    // Getters
    public int getDishId() {
        return dishId;
    }

    public String getName() {
        return name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public java.sql.Date getStartDate() {
        return startDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "dishId=" + dishId +
                ", name='" + name + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
