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
                            <fmt:message key="my-bookings.my-bookings"/>
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
                <c:forEach items="${reservations}" var="reservation" varStatus="reservationStatus">

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title"><fmt:message key="my-bookings.booking"/> #${reservation.getId()}</h3>
                        <div class="card-actions">
                            <c:if test="${reservation.getStatus().getId() == 2}">
                            <a href="invoiceAction?id=${reservation.getId()}" class="btn btn-primary" data-id="${reservation.getId()}">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg><svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-ad-2" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                <path d="M11.933 5h-6.933v16h13v-8"></path>
                                <path d="M14 17h-5"></path>
                                <path d="M9 13h5v-4h-5z"></path>
                                <path d="M15 5v-2"></path>
                                <path d="M18 6l2 -2"></path>
                                <path d="M19 9h2"></path>
                            </svg>
                                <fmt:message key="my-bookings.invoice"/>
                            </a>
                            <a href="#" class="btn btn-primary" data-id="${reservation.getId()}" data-bs-toggle="modal" data-bs-target="#modal-success">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-coin" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                    <circle cx="12" cy="12" r="9"></circle>
                                    <path d="M14.8 9a2 2 0 0 0 -1.8 -1h-2a2 2 0 1 0 0 4h2a2 2 0 1 1 0 4h-2a2 2 0 0 1 -1.8 -1"></path>
                                    <path d="M12 7v10"></path>
                                </svg>
                                <fmt:message key="my-bookings.pay-now"/>
                            </a>
                            </c:if>
                            <c:if test="${reservation.getStatus().getId() == 2 || reservation.getStatus().getId() == 3 }">
                            <a href="#" class="cancel-dialog btn btn-secondary" data-id="${reservation.getId()}" data-bs-toggle="modal" data-bs-target="#modal-danger">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-notes-off" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                    <path d="M7 3h10a2 2 0 0 1 2 2v10m0 4a2 2 0 0 1 -2 2h-10a2 2 0 0 1 -2 -2v-14"></path>
                                    <path d="M11 7h4"></path>
                                    <path d="M9 11h2"></path>
                                    <path d="M9 15h4"></path>
                                    <path d="M3 3l18 18"></path>
                                </svg>
                                <fmt:message key="my-bookings.cancel-reservation"/>
                            </a>
                            </c:if>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="datagrid">
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.checkin-date"/></div>
                                <div class="datagrid-content">${reservation.getCheckinDate()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.checkout-date"/></div>
                                <div class="datagrid-content">${reservation.getCheckoutDate()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.numbers-of-nights"/></div>
                                <div class="datagrid-content">${reservation.getCheckoutDate().toEpochDay() - reservation.getCheckinDate().toEpochDay()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.numbers-of-persons"/></div>
                                <div class="datagrid-content">${reservation.getPersons()}</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.total-price"/></div>
                                <div class="datagrid-content">${reservation.getPrice()}$</div>
                            </div>
                            <div class="datagrid-item">
                                <div class="datagrid-title"><fmt:message key="my-bookings.status"/></div>
                                <div class="datagrid-content">
                      <span class="status status-green">
                              ${statusesTranslated.get(reservation.getStatus())}
                      </span>
                                </div>
                            </div>

                            <div class="datagrid-item<c:if test='${reservation.getStatus().getId() != 2}'> visually-hidden</c:if>">
                                <div class="datagrid-title"><fmt:message key="my-bookings.pay-by"/></div>
                                <div class="datagrid-content">
                                     <span id="time-box${reservation.getId()}" class="status status-orange">
                                     </span>
                                </div>
                            </div>



                        </div>
                        <br>
                        <div class="card">

                            <div class="table-responsive">
                                <table class="table table-vcenter card-table table-striped">
                                    <thead>
                                    <tr>
                                        <th><fmt:message key="my-bookings.room-number"/></th>
                                        <th><fmt:message key="my-bookings.category"/></th>
                                        <th><fmt:message key="my-bookings.occupancy"/></th>
                                        <th><fmt:message key="my-bookings.name"/></th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <c:forEach items="${reservation.getRooms()}" var="room" varStatus="rommStatus">

                                    <tr>
                                        <td>${room.getNumber()}</td>
                                        <td>${room.getRoomCategory().getName()}</td>
                                        <td>${room.getOccupancy()}</td>
                                        <td>${room.getName()}</td>
                                    </tr>

                                    </c:forEach>


                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>
                </div>

                </c:forEach>

                    <div class="card<c:if test="${reservations.size() == 0}"> visually-hidden</c:if>">
                        <!-- Paginator -->
                        <jsp:include page="paginator.jsp"/>
                    </div>

                    <div class="card<c:if test="${reservations.size() > 0}"> visually-hidden</c:if>">
                        <div class="empty">
                            <div class="empty-img"><img src="./static/undraw_quitting_time_dm8t.svg"
                                                        height="128" alt="">
                            </div>
                            <p class="empty-title"><fmt:message key="my-bookings.no-bookings"/></p>
                            <p class="empty-subtitle text-muted">
                                <fmt:message key="my-bookings.go-to-index-page"/>
                                <a href="indexAction"><fmt:message key="my-bookings.page"/></a>
                                <fmt:message key="my-bookings.and-make-your-first-reservation"/>
                            </p>
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
                                <div class="col"><a href="#" id="buttonCancel" class="btn btn-danger w-100">
                                    <fmt:message key="my-bookings.cancel-reservation"/>
                                </a></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal modal-blur fade" id="modal-success" tabindex="-1" style="display: none;" aria-hidden="true">
            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
                <div class="modal-content">
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    <div class="modal-status bg-success"></div>
                    <div class="modal-body text-center py-4">

                        <div class="">
                            <div class="">
                                <div class="mb-3">
                                    <div class="form-label"><fmt:message key="my-bookings.card-number"/></div>
                                    <input type="text" name="input-mask" class="form-control" data-mask="0000 0000 0000 0000" data-mask-visible="true" autocomplete="off" required>
                                </div>
                                <div class="row">
                                    <div class="col-8">
                                        <div class="mb-3">
                                            <label class="form-label"><fmt:message key="my-bookings.expiration-date"/></label>
                                            <div class="row g-2">
                                                <div class="col">
                                                    <select class="form-select" required>
                                                        <option value="1">1</option>
                                                        <option value="2">2</option>
                                                        <option value="3">3</option>
                                                        <option value="4">4</option>
                                                        <option value="5">5</option>
                                                        <option value="6">6</option>
                                                        <option value="7">7</option>
                                                        <option value="8">8</option>
                                                        <option value="9">9</option>
                                                        <option value="10">10</option>
                                                        <option value="11">11</option>
                                                        <option value="12">12</option>
                                                    </select>
                                                </div>
                                                <div class="col">
                                                    <select class="form-select" required>
                                                        <option value="2020">2020</option>
                                                        <option value="2021">2021</option>
                                                        <option value="2022">2022</option>
                                                        <option value="2023">2023</option>
                                                        <option value="2024">2024</option>
                                                        <option value="2025">2025</option>
                                                        <option value="2026">2026</option>
                                                        <option value="2027">2027</option>
                                                        <option value="2028">2028</option>
                                                        <option value="2029">2029</option>
                                                        <option value="2030">2030</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col">
                                        <div class="mb-3">
                                            <div class="form-label">CVV</div>
                                            <input type="number" class="form-control" required>
                                        </div>
                                    </div>
                                </div>
                                <div class="mt-2">
                                    <a href="#" id="buttonPay" class="btn btn-primary w-100">
                                        <fmt:message key="my-bookings.pay-now"/>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"/>
    </div>

    <!-- Libs JS -->

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>

        document.getElementById('modal-danger').addEventListener('show.bs.modal', function(e) {
            document.getElementById('buttonCancel').href = "cancelReservationAction?reservationId=" + e.relatedTarget.dataset.id;
        });

        document.getElementById('modal-success').addEventListener('show.bs.modal', function(e) {
            document.getElementById('buttonPay').href = "paymentAction?reservationId=" + e.relatedTarget.dataset.id;
        });

    </script>

    <script>

        Date.createFromMysql = function(mysql_string) {
            var t, result = null;
            t = mysql_string.split(/[- :]/);
            result = new Date(t[0], t[1] - 1, t[2], t[3] || 0, t[4] || 0, t[5] || 0);
            return result;
        }

        var countDownDates = [<c:forEach items="${reservations}" var="reservation">new Date.createFromMysql("${reservation.getCreatedAt().toString()}").getTime(), </c:forEach> 0];
        var countDownCIDates = [<c:forEach items="${reservations}" var="reservation">new Date.createFromMysql("${reservation.getCheckinDate().toString()}").getTime(), </c:forEach> 0];
        var timeBoxIds = [<c:forEach items="${reservations}" var="reservation">'time-box${reservation.getId()}', </c:forEach> 0];

        // Update the count down every 1 second
        var x = setInterval(function() {


            // Get today's date and time
            var now = new Date().getTime();

            for (var i = 0; i < countDownDates.length - 1; i++) {
                var countDownDate = countDownDates[i];
                // Find the distance between now and the count down date
                var distance = countDownDate - now + (1000 * 60 * 60 * 24 * 2);

                var distance1 = countDownCIDates[i] + 1000 * 60 * 60 * 9 - now;
                if (distance1 < (1000 * 60 * 60 * 24 * 2))
                    distance = distance1;

                // Time calculations for days, hours, minutes and seconds
                var days = Math.floor(distance / (1000 * 60 * 60 * 24));
                var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                var seconds = Math.floor((distance % (1000 * 60)) / 1000);

                           // Output the result in an element with id="demo"
                    document.getElementById(timeBoxIds[i]).innerHTML = days + "d " + hours + "h "
                        + minutes + "m " + seconds + "s ";

                    // If the count down is over, write some text
                    if (distance < 0) {
                        clearInterval(x);
                        document.getElementById(timeBoxIds[i]).innerHTML = "EXPIRED";
                    }
                  }
        }, 1000);
    </script>

</body>
</html>