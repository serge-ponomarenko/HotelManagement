<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${roomCategories == null}"><jsp:forward page="indexAction" /></c:if>

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
                                <h3 class="card-title col"><fmt:message key="categories.list-of-categories"/></h3>
                                <div class="col-2">
                                    <div class="card-actions">
                                        <a href="editCategoryAction?new" class="btn btn-primary">
                                            <!-- Download SVG icon from http://tabler-icons.io/i/plus -->
                                            <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                                            <fmt:message key="categories.add-new"/>
                                        </a>
                                    </div>
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
                                        <th><fmt:message key="categories.name"/></th>
                                        <th><fmt:message key="categories.description"/></th>
                                        <th><fmt:message key="categories.create-date"/></th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <c:forEach items="${roomCategories}" var="category" varStatus="categoryStatus">

                                    <tr>
                                        <td><span class="text-muted">${category.getId()}</span></td>
                                        <td>${category.getName()}</td>
                                        <td>${category.getDescription()}</td>
                                        <td>${category.getCreationDate().toString()}</td>

                                        <td class="text-end">
                                        <span class="dropdown">
                                          <button class="btn dropdown-toggle align-text-top" data-bs-boundary="viewport" data-bs-toggle="dropdown" aria-expanded="true"><fmt:message key="categories.actions"/></button>
                                          <div class="dropdown-menu dropdown-menu-end" style="">
                                                  <a href="editCategoryAction?category_id=${category.getId()}" class="dropdown-item">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><path d="M12 6l4 6l5 -4l-2 10h-14l-2 -10l5 4z"></path></svg>
                                                <fmt:message key="category.edit-category"/>
                                            </a>
                                            <a href="#" class="dropdown-item" data-id="${category.getId()}" data-bs-toggle="modal" data-bs-target="#modal-danger">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="4" y1="7" x2="20" y2="7"></line><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line><path d="M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2 -2l1 -12"></path><path d="M9 7v-3a1 1 0 0 1 1 -1h4a1 1 0 0 1 1 1v3"></path></svg>
                                                <fmt:message key="category.delete-category"/>
                                            </a>
                                          </div>
                                        </span>
                                        </td>
                                    </tr>

                                    </c:forEach>

                                    </tbody>
                                </table>
                            </div>

                            <div class="card-footer d-flex align-items-center">

                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <div class="modal modal-blur fade" id="modal-danger" tabindex="-1" style="display: none;" aria-hidden="true">
            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
                <div class="modal-content">
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    <div class="modal-status bg-danger"></div>
                    <div class="modal-body text-center py-4">
                        <!-- Download SVG icon from http://tabler-icons.io/i/alert-triangle -->
                        <svg xmlns="http://www.w3.org/2000/svg" class="icon mb-2 text-danger icon-lg" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><path d="M12 9v2m0 4v.01"></path><path d="M5 19h14a2 2 0 0 0 1.84 -2.75l-7.1 -12.25a2 2 0 0 0 -3.5 0l-7.1 12.25a2 2 0 0 0 1.75 2.75"></path></svg>
                        <h3><fmt:message key="my-bookings.are-you-sure"/></h3>
                        <div class="text-muted"><fmt:message key="categories.want-to-delete-category"/></div>
                    </div>
                    <div class="modal-footer">
                        <div class="w-100">
                            <div class="row">
                                <div class="col"><a href="#" class="btn w-100" data-bs-dismiss="modal">
                                    <fmt:message key="my-bookings.dismiss"/>
                                </a></div>
                                <div class="col">
                                    <form action="editCategoryAction" method="post">
                                        <input type="hidden" name="category_id" id="categoryIdInput" value="-1">
                                        <input type="hidden" name="action" value="delete">
                                        <button id="buttonCancel" class="btn btn-danger w-100">
                                            <fmt:message key="categories.delete-category"/>
                                        </button>
                                    </form>
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

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>

            document.getElementById('modal-danger').addEventListener('show.bs.modal', function(e) {
            document.getElementById('categoryIdInput').value = e.relatedTarget.dataset.id;
        });

    </script>

</body>
</html>