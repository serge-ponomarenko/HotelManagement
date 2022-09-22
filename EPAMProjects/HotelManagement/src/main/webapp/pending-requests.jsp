<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${requests == null}"><jsp:forward page="indexAction" /></c:if>

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
        <div class="container-xl">
            <!-- Page title -->
            <div class="page-header d-print-none">
                <div class="row g-2 align-items-center">
                    <div class="col">
                        <h2 class="page-title">
                            <fmt:message key="requests.pending-requests"/>
                        </h2>
                        <c:if test="${requests.size() > 0}">
                        <div class="text-muted mt-1"><fmt:message
                                key="index.showed"/> ${paginator.getShowedStart()}-${paginator.getShowedEnd()}
                            <fmt:message key="index.of"/> ${paginator.getResultSize()} <fmt:message
                                    key="requests.requests"/></div>
                        </c:if>
                    </div>
                    <div class="col-2 ">
                        <select name="showBy" class="form-select" onchange="window.location.href = 'pageAction?showBy-${paginator.getPageName()}=' + this.options[this.selectedIndex].value">
                            <c:forEach items="${paginator.getDefaultShowedItemsCount()}" var="itemCount">
                                <option value="${itemCount}"<c:if test="${paginator.getShowBy() == itemCount}"> selected</c:if>><fmt:message
                                        key="index.show-${itemCount}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <div class="page-body">
            <div class="container-xl">

                <div class="row row-cards">
                <c:forEach items="${requests}" var="curRequest" varStatus="curRequestStatus">

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title"><fmt:message key="requests.request"/> #${curRequest.getId()}</h3>
                        <div class="card-actions">

                            <a href="proceedRequestAction?request_id=${curRequest.getId()}" class="btn btn-primary">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><path d="M5.931 6.936l1.275 4.249m5.607 5.609l4.251 1.275"></path><path d="M11.683 12.317l5.759 -5.759"></path><circle cx="5.5" cy="5.5" r="1.5"></circle><circle cx="18.5" cy="5.5" r="1.5"></circle><circle cx="18.5" cy="18.5" r="1.5"></circle><circle cx="8.5" cy="15.5" r="4.5"></circle></svg>
                                <fmt:message key="requests.proceed"/>
                            </a>
                            <a href="proceedRequestAction?request_id=${curRequest.getId()}&action=delete" class="btn btn-primary">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="4" y1="7" x2="20" y2="7"></line><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line><path d="M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2 -2l1 -12"></path><path d="M9 7v-3a1 1 0 0 1 1 -1h4a1 1 0 0 1 1 1v3"></path></svg>
                                <fmt:message key="requests.delete"/>
                            </a>

                        </div>
                    </div>
                    <div class="card-body">
                        <div class="datagrid">
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.checkin-date"/></div>
                                <div class="datagrid-content">${curRequest.getCheckinDate()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.checkout-date"/></div>
                                <div class="datagrid-content">${curRequest.getCheckoutDate()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.numbers-of-nights"/></div>
                                <div class="datagrid-content">${curRequest.getCheckoutDate().toEpochDay() - curRequest.getCheckinDate().toEpochDay()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.numbers-of-persons"/></div>
                                <div class="datagrid-content">${curRequest.getPersons()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="requests.numbers-of-rooms"/></div>
                                <div class="datagrid-content">${curRequest.getRooms()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="requests.room-categories"/></div>
                                <div class="datagrid-content">
                                    <c:forEach items="${curRequest.getRoomCategories()}" var="roomCategory">
                                        ${roomCategory.getName()}<br>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="requests.client"/></div>
                                <div class="datagrid-content">${curRequest.getUser().getFirstName()} ${curRequest.getUser().getLastName()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="requests.additional-information"/></div>
                                <div class="datagrid-content">${curRequest.getAdditionalInformation()}</div>
                            </div>

                        </div>
                        <br>

                    </div>
                </div>

                </c:forEach>

                    <div class="card<c:if test="${requests.size() == 0}"> visually-hidden</c:if>">
                        <div class="card-body">
                        <!-- Paginator -->
                        <jsp:include page="fragments/paginator.jsp"/>
                        </div>
                    </div>

                    <div class="card<c:if test="${requests.size() > 0}"> visually-hidden</c:if>">
                        <div class="empty">
                            <div class="empty-img"><img src="./static/undraw_quitting_time_dm8t.svg"
                                                        height="128" alt="">
                            </div>
                            <p class="empty-title"><fmt:message key="requests.no-requests"/></p>
                            <p class="empty-subtitle text-muted">
                                <fmt:message key="my-bookings.go-to-index-page"/>
                                <a href="indexAction"><fmt:message key="my-bookings.page"/></a>
                            </p>
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