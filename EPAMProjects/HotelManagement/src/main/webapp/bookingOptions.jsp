<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${options == null}"><jsp:forward page="indexAction" /></c:if>

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
                            <h3 class="card-title"><fmt:message key="requests.request"/> #${request.getId()}</h3>
                        </div>
                        <div class="card-body">
                            <div class="datagrid">
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="my-bookings.checkin-date"/></div>
                                    <div class="datagrid-content">${request.getCheckinDate()}</div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="my-bookings.checkout-date"/></div>
                                    <div class="datagrid-content">${request.getCheckoutDate()}</div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="my-bookings.numbers-of-nights"/></div>
                                    <div class="datagrid-content">${request.getCheckoutDate().toEpochDay() - request.getCheckinDate().toEpochDay()}</div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="my-bookings.numbers-of-persons"/></div>
                                    <div class="datagrid-content">${request.getPersons()}</div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="requests.numbers-of-rooms"/></div>
                                    <div class="datagrid-content">${request.getRooms()}</div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="requests.room-categories"/></div>
                                    <div class="datagrid-content">
                                        <c:forEach items="${request.getRoomCategories()}" var="roomCategory">
                                            ${roomCategory.getName()}<br>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="requests.client"/></div>
                                    <div class="datagrid-content">${request.getUser().getFirstName()} ${request.getUser().getLastName()}</div>
                                </div>
                                <div class="datagrid-item">
                                    <div class="datagrid-title"><fmt:message key="requests.additional-information"/></div>
                                    <div class="datagrid-content">${request.getAdditionalInformation()}</div>
                                </div>
                            </div>
                            <br>
                        </div>
                    </div>
                    </div>

                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title col"><fmt:message key="options.request-booking-options"/></h3>
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
                                        <th class="w-1"># <!-- Download SVG icon from http://tabler-icons.io/i/chevron-up -->
                                            <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-sm text-dark icon-thick" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><polyline points="6 15 12 9 18 15"></polyline></svg>
                                        </th>
                                        <th>Rooms</th>
                                        <th>Total occupancy</th>
                                        <th>Price</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <c:forEach items="${roomsNumbers}" var="roomNumbers" varStatus="roomNumbersStatus">

                                    <tr>
                                        <td><span class="text-muted">${roomNumbersStatus.count + (page - 1) * showBy}</span></td>
                                        <td>${roomNumbers}</td>
                                        <td>${occupancies.get(roomNumbersStatus.index)}</td>
                                        <td>${prices.get(roomNumbersStatus.index) * nights}$</td>
                                        <td class="text-end">
                                            <form action="makeReservationFromRequestAction" method="post">
                                                <input type="hidden" name="request_id" value="${request.getId()}">
                                                <c:forEach items='${options.get(roomNumbersStatus.index)}' var='room'>
                                                <input type="hidden" name="room" value="${room.getId()}">
                                                </c:forEach>
                                            <button class="btn btn-primary">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><path d="M9 5h-2a2 2 0 0 0 -2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2 -2v-12a2 2 0 0 0 -2 -2h-2"></path><rect x="9" y="3" width="6" height="4" rx="2"></rect><path d="M9 14l2 2l4 -4"></path></svg>
                                                <fmt:message key="options.make-reservation"/>
                                            </button>
                                            </form>
                                        </td>
                                    </tr>
                                    </c:forEach>

                                    </tbody>
                                </table>
                            </div>

                            <div class="card-footer d-flex align-items-center">
                                <c:if test="${roomsNumbers.size() > 0}">
                                    <div class="text-muted m-0"><fmt:message
                                            key="index.showed"/> ${paginator.getShowedStart()}-${paginator.getShowedEnd()}
                                        <fmt:message key="index.of"/> ${paginator.getResultSize()} <fmt:message
                                                key="options.options"/></div>
                                </c:if>
                                <div class="pagination m-0 ms-auto<c:if test="${roomsNumbers.size() == 0}"> visually-hidden</c:if>">
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