<%@ include file="_header.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<h2>Danh Sách Món Ăn Đang Bán</h2>

<table class="table table-striped table-hover table-bordered">
  <thead class="thead-dark">
  <tr>
    <th>Mã</th>
    <th>Ảnh</th>
    <th>Tên Món</th>
    <th>Danh Mục</th>
    <th>Giá</th>
    <th>Ngày Bán</th>
    <th>Thao Tác</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="dish" items="${dishList}">
    <tr>
      <td>${dish.dishId}</td>
      <td>
        <c:choose>
          <c:when test="${not empty dish.imageUrl}">
            <img src="${dish.imageUrl}" alt="${dish.name}" class="img-thumb">
          </c:when>
          <c:otherwise>
            [No Image]
          </c:otherwise>
        </c:choose>
      </td>
      <td>${dish.name}</td>
      <td>${dish.categoryName}</td>
      <td>
        <fmt:formatNumber value="${dish.price}" pattern="#,###" /> VNĐ
      </td>
      <td>${dish.startDate}</td>
      <td>
        <a href="${pageContext.request.contextPath}/dish?action=edit_form&id=${dish.dishId}"
           class="btn btn-sm btn-info">Sửa</a>
        <a href="${pageContext.request.contextPath}/dish?action=stop_sale&id=${dish.dishId}"
           class="btn btn-sm btn-warning"
           onclick="return confirm('Bạn có chắc muốn DỪNG BÁN món: ${dish.name}?');">Dừng Bán</a>
        <a href="${pageContext.request.contextPath}/dish?action=delete&id=${dish.dishId}"
           class="btn btn-sm btn-danger"
           onclick="return confirm('Bạn có chắc muốn XÓA món: ${dish.name}? (Thao tác này không thể hoàn tác)');">Xóa</a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty dishList}">
    <tr><td colspan="7" class="text-center text-muted">Chưa có món ăn nào đang bán.</td></tr>
  </c:if>
  </tbody>
</table>

<%@ include file="_footer.jsp" %>