<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<% if (request.getAttribute("reservations") == null) response.sendRedirect("indexAction"); %>

<!doctype html>

<html lang="${sessionScope.userSettings.getLocale()}">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"/>
    <title><fmt:message key="index.page-title"/></title>
    <!-- CSS files -->
    <link href="./dist/css/tabler.min.css" rel="stylesheet"/>
    <link href="./dist/css/tabler-flags.min.css" rel="stylesheet"/>
    <link href="./dist/css/tabler-payments.min.css" rel="stylesheet"/>
    <link href="./dist/css/tabler-vendors.min.css" rel="stylesheet"/>
    <link href="./dist/css/demo.min.css" rel="stylesheet"/>
</head>
<body>
<div class="page">

    <!-- Including Page header -->
    <jsp:include page="header.jsp"/>

    <div class="page-wrapper">
        <div class="container-xl">
            <!-- Page title -->
            <div class="page-header d-print-none">
                <div class="row g-2 align-items-center">
                    <div class="col">
                        <h2 class="page-title">
                            <fmt:message key="hotel-occupancy.hotel-occupancy"/>
                        </h2>
                        <c:if test="${reservations.size() > 0}">
                        <div class="text-muted mt-1"><fmt:message
                                key="index.showed"/> ${(page-1) * showBy + 1}-${(page * showBy) > resultSize ? resultSize : page * showBy}
                            <fmt:message key="index.of"/> ${resultSize} <fmt:message
                                    key="my-bookings.reservations"/></div>
                        </c:if>
                    </div>
                    <div class="col-2 ">
                        <select name="showBy" class="form-select" onchange="window.location.href = 'pageAction?showBy=' + this.options[this.selectedIndex].value">
                            <option value="5"<c:if test="${showBy == 5}"> selected</c:if>><fmt:message
                                    key="index.show-5"/></option>
                            <option value="10"<c:if test="${showBy == 10}"> selected</c:if>>
                                <fmt:message key="index.show-10"/></option>
                            <option value="20"<c:if test="${showBy == 20}"> selected</c:if>>
                                <fmt:message key="index.show-20"/></option>
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <div class="page-body">
            <div class="container-xl">

                <div class="row row-cards">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div id="chart-reservations"></div>
                            </div>
                        </div>
                    </div>



                </div>
            </div>
        </div>



        <jsp:include page="footer.jsp"/>
    </div>

    <!-- Libs JS -->
    <script src="./dist/js/apexcharts.min.js" defer></script>
    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>

        document.addEventListener("DOMContentLoaded", function () {

            var options = {
                series: [
                    <c:forEach items="${statusesTranslated.keySet()}" var="status">
                        {
                        name: '${statusesTranslated.get(status)}',
                        data: [
                            <c:forEach items="${reservations.get(status)}" var="reservation" varStatus="reservationStatus">
                                <c:forEach items="${reservation.getRooms()}" var="room" varStatus="roomStatus">
                                    {
                                        x: '${room.getNumber()}',
                                        y: [
                                            new Date('${reservation.getCheckinDate().toString()}').getTime(),
                                            new Date('${reservation.getCheckoutDate().toString()}').getTime()
                                        ]
                                    },
                                </c:forEach>
                            </c:forEach>
                        ]
                    },
                    </c:forEach>

                ],
                chart: {
                    height: 450,
                    type: 'rangeBar'
                },
                plotOptions: {
                    bar: {
                        horizontal: true,
                        barHeight: '80%',
                        rangeBarGroupRows: true,
                        distributed: true
                    }
                },
                xaxis: {
                    type: 'datetime'
                },
                stroke: {
                    width: 1
                },
                fill: {
                    type: 'solid',
                    opacity: 0.7
                },
                legend: {
                    position: 'top',
                    horizontalAlign: 'left'
                }
            };

            var chart = new ApexCharts(document.getElementById('chart-reservations'), options);
            chart.render();

        });



    </script>



</body>
</html>