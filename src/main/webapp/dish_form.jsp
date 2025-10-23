<%@ include file="_header.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:set var="isEdit" value="${dish != null && dish.dishId > 0}" />
<h2>${isEdit ? 'Sửa Thông Tin Món Ăn' : 'Thêm Món Ăn Mới'}</h2>

<c:if test="${not empty errors}">
    <div class="alert alert-danger" role="alert">
        <p class="font-weight-bold">Vui lòng kiểm tra lại thông tin:</p>
        <ul class="mb-0">
            <c:forEach var="error" items="${errors}">
                <li>${error}</li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/dish?action=${isEdit ? 'edit' : 'create'}" method="POST">
    <c:if test="${isEdit}">
        <input type="hidden" name="dishId" value="${dish.dishId}">
    </c:if>

    <div class="form-group">
        <label for="name">Tên Món Ăn <span class="text-danger">(*)</span></label>
        <input type="text" class="form-control" id="name" name="name"
               value="${dish.name}" required>
        <small class="form-text text-muted">Tên món ăn phải dài hơn 7 ký tự.</small>
    </div>

    <div class="form-group">
        <label for="categoryId">Danh Mục <span class="text-danger">(*)</span></label>
        <select class="form-control" id="categoryId" name="categoryId" required>
            <option value="">-- Chọn Danh Mục --</option>
            <c:forEach var="cat" items="${categoryList}">
                <option value="${cat.categoryId}"
                        <c:if test="${cat.categoryId == dish.categoryId}">selected</c:if>>
                        ${cat.categoryName}
                </option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label for="price">Giá Bán <span class="text-danger">(*)</span></label>
        <input type="number" class="form-control" id="price" name="price"
               value="${dish.price > 0 ? dish.price : ''}" required min="0.01" step="0.01">
        <small class="form-text text-muted">Giá bán phải lớn hơn 0.</small>
    </div>

    <div class="form-group">
        <label for="description">Mô Tả</label>
        <textarea class="form-control" id="description" name="description" rows="3">${dish.description}</textarea>
    </div>

    <div class="form-group">
        <label for="imageUrl">URL Ảnh Đại Diện</label>
        <input type="url" class="form-control" id="imageUrl" name="imageUrl" value="${dish.imageUrl}">
    </div>

    <c:if test="${isEdit}">
        <div class="row">
            <div class="form-group col-md-6">
                <label>Ngày Bắt Đầu Bán</label>
                <input type="text" class="form-control" value="${dish.startDate}" disabled>
            </div>
            <div class="form-group col-md-6">
                <label>Ngày Sửa Thông Tin Gần Nhất</label>
                <input type="text" class="form-control" value="<fmt:formatDate value='${dish.modifiedDate}' pattern='dd/MM/yyyy HH:mm:ss' />" disabled>
            </div>
        </div>
    </c:if>

    <button type="submit" class="btn btn-primary">${isEdit ? 'Lưu Thay Đổi' : 'Thêm Món Ăn'}</button>
    <a href="${pageContext.request.contextPath}/dish?action=list" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="_footer.jsp" %>