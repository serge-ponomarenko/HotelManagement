<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${editCategoryMap == null}"><jsp:forward page="indexAction" /></c:if>

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
                <div class="col-12">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                            <c:if test="${categoryId == -1}">
                                <fmt:message key="edit-category.add-new-category"/>
                            </c:if>
                            <c:if test="${categoryId != -1}">
                                <fmt:message key="edit-category.edit-category-id"/> ${categoryId}
                            </c:if>
                            </h3>
                        </div>
                        <div class="card-body">
                            <form action="editCategoryAction" method="post">
                                <input type="hidden" name="category_id" value="${categoryId}">
                            <c:forEach items="${editCategoryMap.keySet()}" var="categoryLocale" varStatus="categoryLocaleStatus">
                            <div class="col-md mt-3">
                                    <div class="card">
                                        <div class="card-header">
                                            <h3 class="card-title"><fmt:message key="edit-category.locale"/>: ${categoryLocale} <span class="flag ${locales.get(categoryLocale).getIconPath()}"></span></h3>
                                        </div>
                                        <div class="card-body">

                                                <div class="form-group mb-2 row">
                                                    <label class="col-3 col-form-label required"><fmt:message key="edit-category.name"/></label>
                                                    <div class="col">
                                                        <input type="text" name="name_${categoryLocale}" class="form-control" placeholder="<fmt:message key="edit-category.enter-name"/>" value="${editCategoryMap.get(categoryLocale).getName()}" required>
                                                    </div>
                                                </div>
                                                <div class="form-group mb-0 row">
                                                    <label class="col-3 col-form-label required"><fmt:message key="edit-category.description"/></label>
                                                    <div class="col">
                                                        <textarea class="form-control" name="description_${categoryLocale}" data-bs-toggle="autosize" placeholder="<fmt:message key="edit-category.enter-description"/>" style="overflow: hidden; overflow-wrap: break-word; resize: none; height: 56px;" spellcheck="false" required>${editCategoryMap.get(categoryLocale).getDescription()}</textarea>
                                                    </div>
                                                </div>

                                        </div>
                                    </div>
                                </div>
                                </c:forEach>
                                <div class="form-footer">
                                    <button type="submit" class="btn btn-primary"><fmt:message key="edit-category.save-category"/></button>
                                </div>
                            </form>
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

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>


    </script>

</body>
</html>