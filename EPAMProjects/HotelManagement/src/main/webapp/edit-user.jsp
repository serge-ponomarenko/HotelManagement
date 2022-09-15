<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<% if (request.getAttribute("editUser") == null) response.sendRedirect("indexAction"); %>

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

        <c:if test="${message != null}">
            <div class="modal modal-blur fade" id="modal-success-message" tabindex="-1" >
                <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        <div class="modal-status bg-success"></div>
                        <div class="modal-body text-center py-4">
                            <!-- Download SVG icon from http://tabler-icons.io/i/circle-check -->
                            <svg xmlns="http://www.w3.org/2000/svg" class="icon mb-2 text-green icon-lg" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><circle cx="12" cy="12" r="9"></circle><path d="M9 12l2 2l4 -4"></path></svg>
                            <h3><fmt:message key="index.succeed"/></h3>
                            <div class="text-muted"><fmt:message key="${message}"/></div>
                        </div>
                        <div class="modal-footer">
                            <div class="w-100">
                                <div class="row">
                                    <div class="col"><a href="#" class="btn w-100" data-bs-dismiss="modal">
                                        <fmt:message key="index.close"/>
                                    </a></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>


        <jsp:include page="fragments/footer.jsp"/>
    </div>

    <!-- Libs JS -->
    <script src="./dist/js/jquery-3.6.1.min.js"></script>

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>


    <c:if test="${message != null}">
    <script type="text/javascript">
        $(window).on('load', function() {
            $('#modal-success-message').modal('show');
        });
    </script>
    </c:if>

    <script>


    </script>

</body>
</html>