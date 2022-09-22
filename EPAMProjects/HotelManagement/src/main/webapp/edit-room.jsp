<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${editRoomMap == null}"><jsp:forward page="indexAction" /></c:if>

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
                            <c:if test="${roomId == -1}">
                                <fmt:message key="edit-room.add-new-room"/>
                            </c:if>
                            <c:if test="${roomId != -1}">
                                <fmt:message key="edit-room.edit-room"/> ${roomId}
                            </c:if>
                            </h3>
                        </div>
                        <div class="card-body">
                            <form action="editRoomAction" method="post">
                                <input type="hidden" name="room_id" value="${roomId}">

                                <div class="col-md mt-3">
                                    <div class="card">
                                      <div class="card-body">
                                            <div class="form-group mb-2 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-room.number"/></label>
                                                <div class="col">
                                                    <input type="text" name="number" class="form-control" placeholder="<fmt:message key="edit-room.enter-number"/>" value="${editRoomMap.get(userSettings.getLocale()).getNumber()}" required>
                                                </div>
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-room.occupancy"/></label>
                                                <div class="col">
                                                    <input type="number" name="occupancy" class="form-control" value="${editRoomMap.get(userSettings.getLocale()).getOccupancy()}" required>
                                                </div>
                                            </div>
                                            <div class="form-group mb-2 row">
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-room.category"/></label>
                                                <div class="col">
                                                    <select name="room-category" class="form-select">
                                                    <c:forEach items="${categories}" var="roomCategory" varStatus="roomCategoryStatus">
                                                            <option value="${roomCategory.getId()}"
                                                                    <c:if test="${roomCategory.getId() == editRoomMap.get(userSettings.getLocale()).getRoomCategory().getId()}"> selected</c:if>
                                                            >
                                                                    ${roomCategory.getName()}
                                                            </option>
                                                    </c:forEach>
                                                    </select>
                                                </div>
                                                <label class="col-3 col-form-label required"><fmt:message key="edit-room.price"/></label>
                                                <div class="col">
                                                    <input type="text" name="price" class="form-control" placeholder="<fmt:message key="edit-room.enter-price"/>" value="${editRoomMap.get('en').getPrice()}" required>
                                                </div>
                                            </div>


                                        </div>
                                    </div>
                                </div>

                            <c:forEach items="${editRoomMap.keySet()}" var="roomLocale" varStatus="roomLocaleStatus">
                            <div class="col-md mt-3">
                                    <div class="card">
                                        <div class="card-header">
                                            <h3 class="card-title"><fmt:message key="edit-category.locale"/>: ${roomLocale} <span class="flag ${locales.get(roomLocale).getIconPath()}"></span></h3>
                                        </div>
                                        <div class="card-body">

                                                <div class="form-group mb-2 row">
                                                    <label class="col-3 col-form-label required"><fmt:message key="edit-category.name"/></label>
                                                    <div class="col">
                                                        <input type="text" name="name_${roomLocale}" class="form-control" placeholder="<fmt:message key="edit-category.enter-name"/>" value="${editRoomMap.get(roomLocale).getName()}" required>
                                                    </div>
                                                </div>
                                                <div class="form-group mb-0 row">
                                                    <label class="col-3 col-form-label required"><fmt:message key="edit-category.description"/></label>
                                                    <div class="col">
                                                        <textarea class="form-control" name="description_${roomLocale}" data-bs-toggle="autosize" placeholder="<fmt:message key="edit-category.enter-description"/>" style="overflow: hidden; overflow-wrap: break-word; resize: none; height: 56px;" spellcheck="false" required>${editRoomMap.get(roomLocale).getDescription()}</textarea>
                                                    </div>
                                                </div>

                                        </div>
                                    </div>
                                </div>

                                </c:forEach>

                                <div class="form-footer">
                                    <button type="submit" class="btn btn-primary"><fmt:message key="edit-room.save-room"/></button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-12 mt-3">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <fmt:message key="edit-room.photos"/>
                            </h3>
                        </div>
                        <div class="card-body">


                                <div class="col-md mt-3">
                                    <div class="card">
                                        <div class="card-body">
                                            <div class="row row-cards">
                                            <div class="col-6">
                                                <div class="card">
                                                <div class="table-responsive">
                                                    <table class="table card-table table-vcenter text-nowrap datatable">
                                                        <thead>
                                                        <tr>
                                                            <th class="w-1"># <!-- Download SVG icon from http://tabler-icons.io/i/chevron-up -->
                                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-sm text-dark icon-thick" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><polyline points="6 15 12 9 18 15"></polyline></svg>
                                                            </th>
                                                            <th><fmt:message key="edit-room.photo"/></th>
                                                            <th><fmt:message key="edit-room.path"/></th>
                                                            <th></th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>

                                                        <c:forEach items="${editRoomMap.get(userSettings.getLocale()).getImages()}" var="image" varStatus="imageStatus">

                                                            <tr>
                                                                <td><span class="text-muted">${imageStatus.count}</span></td>
                                                                <td><img class="avatar" src="${image}"></td>
                                                                <td>${image}</td>

                                                                <td class="text-end">
                                                            <span class="dropdown">
                                                              <button class="btn dropdown-toggle align-text-top" data-bs-boundary="viewport" data-bs-toggle="dropdown" aria-expanded="true"><fmt:message key="categories.actions"/></button>
                                                              <div class="dropdown-menu dropdown-menu-end" style="">
                                                                  <form action="FileUploadServlet" method="post">
                                                                      <input type="hidden" name="">
                                                                <button type="submit" class="dropdown-item">
                                                                    <input type="hidden" name="room_id" value="${roomId}">
                                                                    <input type="hidden" name="action" value="delete">
                                                                    <input type="hidden" name="path" value="${image}">
                                                                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="4" y1="7" x2="20" y2="7"></line><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line><path d="M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2 -2l1 -12"></path><path d="M9 7v-3a1 1 0 0 1 1 -1h4a1 1 0 0 1 1 1v3"></path></svg>
                                                                    <fmt:message key="edit-room.delete-photo"/>
                                                                </button>
                                                                  </form>
                                                              </div>
                                                            </span>
                                                                </td>
                                                            </tr>

                                                        </c:forEach>

                                                        </tbody>
                                                    </table>
                                                </div>
                                                </div>
                                            </div>

                                            <div class="col-6">
                                                <div class="card">
                                                    <div class="card-body">
                                                <h3 class="card-title"><fmt:message key="edit-room.add-photo"/></h3>
                                                        <form action="FileUploadServlet" method="post" enctype="multipart/form-data">
                                                            <input type="hidden" name="room_id" value="${roomId}">
                                                        <input type="file" class="form-control" name="filename" required>
                                                        <button type="submit" class="mt-2 form-control btn btn-primary"><fmt:message key="edit-room.add-photo"/></button>
                                                        </form>
                                                    </div>
                                                </div>

                                            </div>
                                            </div>
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

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>


    <script>


    </script>

</body>
</html>