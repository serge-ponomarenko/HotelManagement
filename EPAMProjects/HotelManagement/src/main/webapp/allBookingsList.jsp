<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${reservations == null}"><jsp:forward page="indexAction" /></c:if>

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
                                <h3 class="card-title col"><fmt:message key="all-bookings.all-bookings"/></h3>
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
                                        <th class="w-1"><a <myTags:sort-icon field_name="id" paginator="${paginator}" />>ID</a></th>
                                        <th><a <myTags:sort-icon field_name="checkin" paginator="${paginator}" />><fmt:message key="all-bookings.chekin-date"/></a></th>
                                        <th><a <myTags:sort-icon field_name="checkout" paginator="${paginator}" />><fmt:message key="all-bookings.chekout-date"/></a></th>
                                        <th><a <myTags:sort-icon field_name="nights" paginator="${paginator}" />><fmt:message key="all-bookings.nights"/></a></th>
                                        <th><a <myTags:sort-icon field_name="persons" paginator="${paginator}" />><fmt:message key="all-bookings.persons"/></a></th>
                                        <th><a <myTags:sort-icon field_name="status" paginator="${paginator}" />><fmt:message key="all-bookings.status"/></a></th>
                                        <th><fmt:message key="all-bookings.rooms"/></th>
                                        <th><a <myTags:sort-icon field_name="price" paginator="${paginator}" />><fmt:message key="all-bookings.price"/></a></th>
                                        <th><a <myTags:sort-icon field_name="client" paginator="${paginator}" />><fmt:message key="all-bookings.client"/></a></th>
                                        <%--<th><fmt:myTags key="rooms.creation_date"/></th>--%>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <c:forEach items="${reservations}" var="reservation" varStatus="reservationStatus">

                                        <tr>
                                            <td><span class="text-muted">${reservation.getId()}</span></td>
                                            <td>${reservation.getCheckinDate()}</td>
                                            <td>${reservation.getCheckoutDate()}</td>
                                            <td>${reservation.getCheckoutDate().toEpochDay() - reservation.getCheckinDate().toEpochDay()}</td>
                                            <td>${reservation.getPersons()}</td>
                                            <td>${statusesTranslated.get(reservation.getStatus())}</td>
                                            <td>|
                                            <c:forEach items="${reservation.getRooms()}" var="room" varStatus="rommStatus">
                                             ${room.getNumber()} |
                                            </c:forEach>
                                            </td>
                                            <td>${reservation.getPrice()}$</td>
                                            <td>${reservation.getUser().getFirstName()} ${reservation.getUser().getLastName()}</td>

                                            <td class="text-end">
                                        <span class="dropdown">
                                          <button class="btn dropdown-toggle align-text-top" data-bs-boundary="viewport" data-bs-toggle="dropdown" aria-expanded="true"><fmt:message key="categories.actions"/></button>
                                          <div class="dropdown-menu dropdown-menu-end" style="">
                                               <form action="paymentAction" method="post">
                                                <input type="hidden" name="reservationId" value="${reservation.getId()}">
                                                <input type="hidden" name="userId" value="${reservation.getUser().getId()}">
                                                  <button type="submit" class="dropdown-item">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><path d="M12 6l4 6l5 -4l-2 10h-14l-2 -10l5 4z"></path></svg>
                                                <fmt:message key="all-bookings.mark-paid"/>
                                                </button>
                                               </form>
                                            <a href="#" class="dropdown-item" data-id="${reservation.getId()}" data-userid="${reservation.getUser().getId()}" data-bs-toggle="modal" data-bs-target="#modal-danger">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="4" y1="7" x2="20" y2="7"></line><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line><path d="M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2 -2l1 -12"></path><path d="M9 7v-3a1 1 0 0 1 1 -1h4a1 1 0 0 1 1 1v3"></path></svg>
                                                <fmt:message key="all-bookings.cancel-booking"/>
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
                                <c:if test="${rooms.size() > 0}">
                                    <div class="text-muted m-0"><fmt:message
                                            key="index.showed"/> ${paginator.getShowedStart()}-${paginator.getShowedEnd()}
                                        <fmt:message key="index.of"/> ${paginator.getResultSize()} <fmt:message
                                                key="rooms.rooms"/></div>
                                </c:if>
                                <div class="pagination m-0 ms-auto<c:if test="${rooms.size() == 0}"> visually-hidden</c:if>">
                                    <!-- Paginator -->
                                    <jsp:include page="fragments/paginator.jsp"/>
                                </div>
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
                        <div class="text-muted"><fmt:message key="my-bookings.want-to-cancel-reservation"/></div>
                    </div>
                    <div class="modal-footer">
                        <div class="w-100">
                            <div class="row">
                                <div class="col"><a href="#" class="btn w-100" data-bs-dismiss="modal">
                                    <fmt:message key="my-bookings.dismiss"/>
                                </a></div>
                                <div class="col">
                                    <form action="cancelReservationAction" method="post">
                                        <input type="hidden" name="reservationId" id="reservationIdInput" value="-1">
                                        <input type="hidden" name="userId" id="userIdInput" value="-1">
                                    <button id="buttonCancel" class="btn btn-danger w-100">
                                    <fmt:message key="my-bookings.cancel-reservation"/>
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
            document.getElementById('reservationIdInput').value = e.relatedTarget.dataset.id;
            document.getElementById('userIdInput').value = e.relatedTarget.dataset.userid;
        });

    </script>

</body>
</html>