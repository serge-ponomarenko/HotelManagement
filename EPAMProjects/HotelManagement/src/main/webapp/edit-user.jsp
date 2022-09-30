<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${editUser == null}"><jsp:forward page="indexAction" /></c:if>

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
                <div class="page-header mb-3">
                    <div class="row align-items-center mw-100">
                        <div class="col">
                            <div class="mb-1">
                                <ol class="breadcrumb" aria-label="breadcrumbs">
                                    <li class="breadcrumb-item"><a href="#"><fmt:message key="edit-user.administration"/></a></li>
                                    <li class="breadcrumb-item active" aria-current="page"><a href="#"><fmt:message key="edit-user.settings"/></a></li>
                                </ol>
                            </div>
                            <h2 class="page-title">
                                <span class="text-truncate"><fmt:message key="edit-user.user-settings"/></span>
                            </h2>
                        </div>
                        <div class="col-auto">
                            <%--<div class="btn-list">
                                <button class="btn btn-primary">
                                    <fmt:message key="edit-user.save-settings"/>
                                </button>
                            </div>--%>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-12 col-md-3 px-3">
                        <div class="list-group list-group-transparent mb-3 ml-3">
                            <a class="list-group-item list-group-item-action d-flex align-items-center active" href="#">
                                <fmt:message key="edit-user.account"/>
                            </a>
                            <a class="list-group-item list-group-item-action d-flex align-items-center" href="#">
                                <fmt:message key="edit-user.preferences"/>
                            </a>
                            <a class="list-group-item list-group-item-action d-flex align-items-center" href="#">
                                <fmt:message key="edit-user.theme"/>
                            </a>
                        </div>
                    </div>
                    <div class="col-12 col-md-9">
                        <div class="row">
                            <div class="col-12">
                                <div class="card">
                                     <div class="card-header">
                                         <h3 class="card-title"><fmt:message key="edit-user.account"/></h3>
                                     </div>
                                    <div class="card-body">
                                        <form action="editUserAction" method="post">
                                            <input type="hidden" name="user_id" value="${editUser.getId()}">
                                            <div class="form-group mb-3 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-user.first-name"/></label>
                                                <div class="col">
                                                    <input type="text" name="firstName" class="form-control" value="${editUser.getFirstName()}" placeholder="" required>
                                                </div>
                                            </div>
                                            <div class="form-group mb-3 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-user.last-name"/></label>
                                                <div class="col">
                                                    <input type="text" name="lastName" class="form-control" value="${editUser.getLastName()}" placeholder="" required>
                                                </div>
                                            </div>
                                            <div class="form-group mb-3 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-user.email-address"/></label>
                                                <div class="col">
                                                    <input type="email" class="form-control" value="${editUser.getEmail()}" aria-describedby="emailHelp" disabled>
                                                    <small class="form-hint"><fmt:message key="edit-user.we-share-your-email"/></small>
                                                </div>
                                            </div>
                                            <div class="form-group mb-3 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-user.old-password"/></label>
                                                <div class="col">
                                                    <input type="password" name="oldPassword" class="form-control" placeholder="Password">
                                                    <small class="form-hint">
                                                        <fmt:message key="edit-user.your-old-password-only"/>
                                                    </small>
                                                </div>
                                            </div>
                                            <div class="form-group mb-3 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-user.new-password"/></label>
                                                <div class="col">
                                                    <input type="password" name="newPassword" class="form-control" placeholder="Password">
                                                    <small class="form-hint">
                                                        <fmt:message key="edit-user.your-password-must-be"/>
                                                    </small>
                                                </div>
                                            </div>

                                            <c:if test="${user.getRole().toString().equals('ADMINISTRATOR')}">
                                            <div class="form-group mb-3 row">
                                                <label class="col-3 col-form-label"><fmt:message key="edit-user.role"/></label>
                                                <div class="col">
                                                    <select class="form-select" name="role">
                                                        <c:forEach items="${roles}" var="role" >
                                                            <option value="${role}"<c:if test="${role == editUser.getRole().toString()}"> selected</c:if>>${role}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>
                                            </c:if>

                                            <div class="form-footer">
                                                <button type="submit" class="btn btn-primary"><fmt:message key="edit-user.submit"/></button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

         <jsp:include page="fragments/footer.jsp"/>

        <myTags:success_message message="${success_message}" />
        <myTags:fail_message message="${fail_message}" />
    </div>

    <!-- Libs JS -->
    <script src="./dist/js/jquery-3.6.1.min.js"></script>

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>


    <script>


    </script>

</body>
</html>