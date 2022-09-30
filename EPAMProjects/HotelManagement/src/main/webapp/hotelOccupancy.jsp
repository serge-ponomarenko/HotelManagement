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
        <div class="container-xl">
            <!-- Page title -->
            <div class="page-header d-print-none">
                <div class="row g-2 align-items-center">
                    <div class="col">
                        <h2 class="page-title">
                            <fmt:message key="hotel-occupancy.hotel-occupancy"/>
                        </h2>
                    </div>

                    <div class="col-3">
                        <div class="card-actions">
                            <a href="#" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modal-report">
                                <!-- Download SVG icon from http://tabler-icons.io/i/plus -->
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><rect x="5" y="3" width="14" height="6" rx="2"></rect><path d="M19 6h1a2 2 0 0 1 2 2a5 5 0 0 1 -5 5l-5 0v2"></path><rect x="10" y="15" width="4" height="6" rx="1"></rect></svg>
                                <fmt:message key="hotel-occupancy.set-maintenance"/>
                            </a>
                        </div>
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


        <div class="modal modal-blur fade" id="modal-report" tabindex="-1" style="display: none;" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
                <div class="modal-content">
                    <form action="setMaintenanceAction" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title"><fmt:message key="hotel-occupancy.set-maintenance"/></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>

                        <div class="modal-body">

                            <div class="row">
                                <div class="col-lg-6">
                                    <div class="mb-3">
                                        <label class="form-label"><fmt:message key="hotel-occupancy.start-date"/></label>
                                        <div class="input-icon mb-2">
                                            <input class="form-control" name="checkin-date"
                                                   placeholder="<fmt:message key="index.select-date"/>" id="datepicker-start2"
                                                   value="${param['checkin-date']}" autocomplete="off" required>
                                            <span class="input-icon-addon"><!-- Download SVG icon from http://tabler-icons.io/i/calendar -->
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24"
                                                     viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none"
                                                     stroke-linecap="round" stroke-linejoin="round"><path stroke="none"
                                                                                                          d="M0 0h24v24H0z"
                                                                                                          fill="none"></path><rect x="4"
                                                                                                                                   y="5"
                                                                                                                                   width="16"
                                                                                                                                   height="16"
                                                                                                                                   rx="2"></rect><line
                                                        x1="16" y1="3" x2="16" y2="7"></line><line x1="8" y1="3" x2="8" y2="7"></line><line
                                                        x1="4" y1="11" x2="20" y2="11"></line><line x1="11" y1="15" x2="12"
                                                                                                    y2="15"></line><line x1="12" y1="15"
                                                                                                                         x2="12"
                                                                                                                         y2="18"></line></svg>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-lg-6">
                                    <div class="mb-3">
                                        <label class="form-label"><fmt:message key="hotel-occupancy.end-date"/></label>
                                        <div class="input-icon mb-2">
                                            <input class="form-control" name="checkout-date"
                                                   placeholder="<fmt:message key="index.select-date"/>" id="datepicker-end2"
                                                   value="${param['checkout-date']}" autocomplete="off" required>
                                            <span class="input-icon-addon"><!-- Download SVG icon from http://tabler-icons.io/i/calendar -->
                                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24"
                                                     viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none"
                                                     stroke-linecap="round" stroke-linejoin="round"><path stroke="none"
                                                                                                          d="M0 0h24v24H0z"
                                                                                                          fill="none"></path><rect x="4"
                                                                                                                                   y="5"
                                                                                                                                   width="16"
                                                                                                                                   height="16"
                                                                                                                                   rx="2"></rect><line
                                                        x1="16" y1="3" x2="16" y2="7"></line><line x1="8" y1="3" x2="8" y2="7"></line><line
                                                        x1="4" y1="11" x2="20" y2="11"></line><line x1="11" y1="15" x2="12"
                                                                                                    y2="15"></line><line x1="12" y1="15"
                                                                                                                         x2="12"
                                                                                                                         y2="18"></line></svg>
                                          </span>
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <div class="col-lg-12">
                                <label class="form-label"><fmt:message key="hotel-occupancy.room-number"/></label>
                                <div class="mb-3">

                                    <div class="form-group mb-2 row">

                                        <div class="col">
                                            <select name="room" class="form-select">
                                                <c:forEach items="${allRooms}" var="room">
                                                    <option value="${room.getId()}">
                                                            ${room.getNumber()}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                    </div>

                                </div>
                            </div>


                        </div>
                        <div class="modal-footer">
                            <a href="#" class="btn btn-link link-secondary" data-bs-dismiss="modal">
                                <fmt:message key="index.cancel"/>
                            </a>
                            <button type="submit" class="btn btn-primary ms-auto">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><rect x="5" y="3" width="14" height="6" rx="2"></rect><path d="M19 6h1a2 2 0 0 1 2 2a5 5 0 0 1 -5 5l-5 0v2"></path><rect x="10" y="15" width="4" height="6" rx="1"></rect></svg>
                                <fmt:message key="hotel-occupancy.set"/>
                            </button>
                        </div>
                    </form>
                </div>

            </div>
        </div>



        <jsp:include page="fragments/footer.jsp"/>

        <myTags:success_message message="${success_message}" />
        <myTags:fail_message message="${fail_message}" />
    </div>

    <!-- Libs JS -->
    <script src="./dist/js/apexcharts.min.js" defer></script>
    <script src="./dist/js/litepicker.js" defer></script>
    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>

        document.addEventListener("DOMContentLoaded", function () {

            var series = [
                <c:forEach items="${statusesTranslated.keySet()}" var="status">
                {
                    name: '${statusesTranslated.get(status)}',

                    data: [
                        <c:if test="${status.toString().equals('FREE')}">
                        <c:forEach items="${freeRooms}" var="room">
                        {
                            x: '${room.getNumber()}',
                            y: [new Date('${minDate.toString()}').getTime(),
                                new Date('${maxDate.toString()}').getTime() ],
                            reservationId: '-',
                            userName: '-',
                            checkinDate: '-',
                            checkoutDate: '-',
                            nights: '-',
                            persons: '-',
                            price: '-',
                            status: 'FREE'
                        },
                        </c:forEach>
                        </c:if>

                        <c:forEach items="${reservations.get(status)}" var="reservation" varStatus="reservationStatus">
                        <c:forEach items="${reservation.getRooms()}" var="room" varStatus="roomStatus">
                        {
                            x: '${room.getNumber()}',
                            y: [
                                new Date('${reservation.getCheckinDate().toString()}').getTime(),
                                new Date('${reservation.getCheckoutDate().toString()}').getTime()
                            ],
                            reservationId: '${reservation.getId()}',
                            userName: '${reservation.getUser().getFirstName()} ${reservation.getUser().getLastName()}',
                            checkinDate: '${reservation.getCheckinDate().toString()}',
                            checkoutDate: '${reservation.getCheckoutDate().toString()}',
                            nights: '${reservation.getCheckoutDate().toEpochDay() - reservation.getCheckinDate().toEpochDay()}',
                            persons: '${reservation.getPersons()}',
                            price: '${reservation.getPrice()}',
                            status: '${reservation.getStatus().toString()}'

                        },
                        </c:forEach>
                        </c:forEach>
                    ]
                },
                </c:forEach>

            ];

            var options = {
                series: series,

                chart: {
                    height: 450,
                    type: 'rangeBar'
                },
                plotOptions: {
                    bar: {
                        horizontal: true,
                        barHeight: '80%',
                        rangeBarGroupRows: true
                    }
                },
                grid: {
                    borderColor: '#AAA',
                    strokeDashArray: 2,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    },
                    xaxis: {
                        lines: {
                            show: true
                        }
                    },
                },
                xaxis: {
                    type: 'datetime',
                    labels: {
                        datetimeFormatter: {
                            year: 'yyyy',
                            month: 'MM.yyyy',
                            day: 'dd.MM',
                            hour: 'HH:mm'
                        }
                    }
                },
                colors: [
                    "#555555", "#e36600", "#ef00c6", "#fd2343",
                    "#0009b9", "#28ce05", "#ffe666"
                ],
                annotations: {
                    xaxis: [
                        {
                            x: new Date().getTime(),
                            borderColor: '#167a00',
                            width: 4,
                            label: {
                                style: {
                                    color: '#0d9400',
                                    width: 2
                                },
                                text: 'today'
                            }
                        }
                    ]
                },
                tooltip: {
                    custom: function({series, seriesIndex, dataPointIndex, w}) {
                        var data = w.globals.initialSeries[seriesIndex].data[dataPointIndex];

                        return '<ul>' +
                            '<li><b>Reservation #</b> ' + data.reservationId + '</li>' +
                            '<li><b>Room #</b> ' + data.x + '</li>' +
                            '<li><b>Checkin</b>: ' + data.checkinDate + '</li>' +
                            '<li><b>Checkout</b>: ' + data.checkoutDate + '</li>' +
                            '<li><b>Nights</b>: ' + data.nights + '</li>' +
                            '<li><b>Persons</b>: ' + data.persons + '</li>' +
                            '<li><b>Total price</b>: ' + data.price + '</li>' +
                            '<li><b>User</b>: ' + data.userName + '</li>' +
                            '<li><b>Status</b>: ' + data.status + '</li>' +
                            '</ul>';
                    }
                },
                stroke: {
                    width: 1
                },
                fill: {
                    type: 'solid',
                    opacity: 0.8
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
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            window.Litepicker && (new Litepicker({
                element: document.getElementById('datepicker-start2'),
                elementEnd: document.getElementById('datepicker-end2'),
                singleMode: false,
                tooltipText: {
                    one: '<fmt:message key="index.night"/>',
                    other: '<fmt:message key="index.nights"/>'
                },
                tooltipNumber: (totalDays) => {
                    return totalDays - 1;
                },
                minDate: Date(),
                buttonText: {
                    previousMonth: `
    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><polyline points="15 6 9 12 15 18" /></svg>`,
                    nextMonth: `
    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><polyline points="9 6 15 12 9 18" /></svg>`,
                },
            }));
        });
    </script>


</body>
</html>