<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<% if (request.getAttribute("users") == null) response.sendRedirect("indexAction"); %>

<!doctype html>

<html lang="${sessionScope.userSettings.getLocale()}">
<head>
    <%@ include file="fragments/head.jsp" %>
</head>
<body>
<div class="page">

    <!-- Including Page header -->
    <jsp:include page="fragments/header.jsp"/>

    <div class="page-wrapper">

        <div class="page-body">
            <div class="container-xl">

                <div class="row row-cards">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title col"><fmt:message key="users.list-of-users"/></h3>
                                <div class="col-2">
                                    <select name="showBy" class="form-select" onchange="window.location.href = 'pageAction?showBy-${paginator.getPageName()}=' + this.options[this.selectedIndex].value">
                                        <c:forEach items="${paginator.getDefaultShowedItemsCount()}" var="itemCount">
                                            <option value="${itemCount}"<c:if test="${paginator.getShowBy() == itemCount}"> selected</c:if>><fmt:message
                                                    key="index.show-${itemCount}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="card-body border-bottom py-3">

                            </div>
                            <div class="table-responsive">
                                <table class="table card-table table-vcenter text-nowrap datatable">
                                    <thead>
                                    <tr>
                                        <th class="w-1">ID <!-- Download SVG icon from http://tabler-icons.io/i/chevron-up -->
                                            <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-sm text-dark icon-thick" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><polyline points="6 15 12 9 18 15"></polyline></svg>
                                        </th>
                                        <th><fmt:message key="users.email"/></th>
                                        <th><fmt:message key="users.first-name"/></th>
                                        <th><fmt:message key="users.last-name"/></th>
                                        <th><fmt:message key="users.role"/></th>
                                        <th><fmt:message key="users.register-date"/></th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <c:forEach items="${users}" var="user" varStatus="userStatus">

                                    <tr>
                                        <td><span class="text-muted">${user.getId()}</span></td>
                                        <td>${user.getEmail()}</td>
                                        <td>${user.getFirstName()}</td>
                                        <td>${user.getLastName()}</td>
                                        <td>${user.getRole()}</td>
                                        <td>${user.getRegisteredDate().toString()}</td>
                                        <td class="text-end">
                                            <a href="editUserAction?user_id=${user.getId()}" class="btn btn-primary">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><circle cx="12" cy="7" r="4"></circle><path d="M6 21v-2a4 4 0 0 1 4 -4h4a4 4 0 0 1 4 4v2"></path></svg>
                                                <fmt:message key="users.edit-user"/>
                                            </a>
                                        </td>
                                    </tr>

                                    </c:forEach>

                                    </tbody>
                                </table>
                            </div>

                            <div class="card-footer d-flex align-items-center">
                                <c:if test="${users.size() > 0}">
                                    <div class="text-muted m-0"><fmt:message
                                            key="index.showed"/> ${paginator.getShowedStart()}-${paginator.getShowedEnd()}
                                        <fmt:message key="index.of"/> ${paginator.getResultSize()} <fmt:message
                                                key="users.users"/></div>
                                </c:if>
                                <div class="pagination m-0 ms-auto<c:if test="${users.size() == 0}"> visually-hidden</c:if>">
                                    <!-- Paginator -->
                                    <jsp:include page="fragments/paginator.jsp"/>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>



        <jsp:include page="fragments/footer.jsp"/>
    </div>

    <!-- Libs JS -->

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>


    </script>

</body>
</html>