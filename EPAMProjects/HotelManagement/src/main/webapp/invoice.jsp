<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${reservation == null}"><jsp:forward page="indexAction" /></c:if>

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
                    <div class="col-8">
                        <h2 class="page-title">
                            <fmt:message key="invoice.invoice"/>
                        </h2>
                    </div>
                    <!-- Page title actions -->
                    <div class="col-2 d-print-none">
                        <button type="button" class="btn btn-primary" onclick="javascript:window.print();">
                            <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M17 17h2a2 2 0 0 0 2 -2v-4a2 2 0 0 0 -2 -2h-14a2 2 0 0 0 -2 2v4a2 2 0 0 0 2 2h2" /><path d="M17 9v-4a2 2 0 0 0 -2 -2h-6a2 2 0 0 0 -2 2v4" /><rect x="7" y="13" width="10" height="8" rx="2" /></svg>
                            <fmt:message key="invoice.print-invoice"/>
                        </button>
                    </div>
                    
                    <c:if test="${!reservation.getStatus().toString().equals('PAID')}">
                    <div class="col-2 d-print-none">
                    <form action="paymentAction" method="post">
                        <input type="hidden" name="reservationId" value="${reservation.getId()}">
                        <input type="hidden" name="userId" value="${reservation.getUser().getId()}">
                    <button class="d-print-none btn btn-primary">
                        <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-coin" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <circle cx="12" cy="12" r="9"></circle>
                            <path d="M14.8 9a2 2 0 0 0 -1.8 -1h-2a2 2 0 1 0 0 4h2a2 2 0 1 1 0 4h-2a2 2 0 0 1 -1.8 -1"></path>
                            <path d="M12 7v10"></path>
                        </svg>
                        <fmt:message key="invoice.report-payment"/>
                    </button>
                    </form>
                    </div>
                    </c:if>
                    
                    
                </div>
            </div>
        </div>

        <div class="page-body">

            <div class="page-body">
                <div class="container-xl">
                    <div class="card card-lg">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-6">
                                    <p class="h3"><fmt:message key="invoice.company"/> "Hotel"</p>
                                    <address>
                                        <fmt:message key="invoice.street"/><br>
                                        <fmt:message key="invoice.city"/><br>
                                        <fmt:message key="invoice.zip"/><br>
                                        <fmt:message key="invoice.email"/>
                                    </address>
                                </div>
                                <div class="col-6 text-end">
                                    <p class="h3"><fmt:message key="invoice.client"/></p>
                                    <address>
                                        ${sessionScope.user.getFirstName()} ${sessionScope.user.getLastName()}<br>
                                        ${sessionScope.user.getEmail()}<br>
                                        <br>
                                            <i><fmt:message key="invoice.club-card-number"/> #${sessionScope.user.getId()}</i>
                                    </address>
                                </div>
                                <div class="col-12 my-5">
                                    <h1><fmt:message key="invoice.invoice"/> INV#${reservation.getId()} - ${reservation.getCheckinDate().toString()} </h1>
                                </div>
                            </div>
                            <table class="table table-transparent table-responsive">
                                <thead>
                                <tr>
                                    <th class="text-center" style="width: 1%"></th>
                                    <th><fmt:message key="invoice.service"/></th>
                                    <th class="text-center" style="width: 1%"><fmt:message key="invoice.nights"/></th>
                                    <th class="text-end" style="width: 1%"><fmt:message key="invoice.unit"/></th>
                                    <th class="text-end" style="width: 1%"><fmt:message key="invoice.amount"/></th>
                                </tr>
                                </thead>
                                <c:forEach items="${reservation.getRooms()}" var="room" varStatus="roomStatus">
                                <tr>
                                    <td class="text-center">${roomStatus.count}</td>
                                    <td>
                                        <p class="strong mb-1"><fmt:message key="invoice.room"/> #${room.getNumber()} - ${room.getName()}</p>
                                        <div class="text-muted"><fmt:message key="invoice.room-category"/>: ${room.getRoomCategory().getName()} • <fmt:message key="invoice.occupancy"/>: ${room.getOccupancy()} • <fmt:message key="invoice.checkin"/>: ${reservation.getCheckinDate().toString()} • <fmt:message key="invoice.checkout"/>: ${reservation.getCheckoutDate().toString()}</div>
                                    </td>
                                    <td class="text-center">
                                            ${reservation.getCheckoutDate().toEpochDay() - reservation.getCheckinDate().toEpochDay()}
                                    </td>
                                    <td class="text-end">$${String.format("%,.2f", room.getPrice())}</td>
                                    <td class="text-end">$${String.format("%,.2f", room.getPrice() * (reservation.getCheckoutDate().toEpochDay() - reservation.getCheckinDate().toEpochDay()))}</td>
                                </tr>
                                </c:forEach>

                                <tr>
                                    <td colspan="4" class="strong text-end"><fmt:message key="invoice.subtotal"/></td>
                                    <td class="text-end">$${String.format("%,.2f", reservation.getPrice())}</td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="strong text-end"><fmt:message key="invoice.vat-rate"/></td>
                                    <td class="text-end">20%</td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="strong text-end"><fmt:message key="invoice.vat-due"/></td>
                                    <td class="text-end">$${String.format("%,.2f", reservation.getPrice().multiply(0.2))}</td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="font-weight-bold text-uppercase text-end"><fmt:message key="invoice.total-due"/></td>
                                    <td class="font-weight-bold text-end">$${String.format("%,.2f", reservation.getPrice().multiply(1.2))}</td>
                                </tr>
                            </table>
                            <p class="text-muted text-center mt-5"><fmt:message key="invoice.thank-you"/></p>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Including Page footer -->
            <jsp:include page="fragments/footer.jsp"/>

            <myTags:success_message message="${success_message}" />
            <myTags:fail_message message="${fail_message}" />

        </div>
    </div>
    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

</body>
</html>