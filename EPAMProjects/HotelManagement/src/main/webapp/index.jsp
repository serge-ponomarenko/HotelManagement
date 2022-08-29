<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<% if(request.getAttribute("rooms")==null) response.sendRedirect("indexAction"); %>

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
                            <fmt:message key="index.find-your-stay"/>
                        </h2>
                        <div class="text-muted mt-1"><fmt:message key="index.choose-your-room-parameters"/></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="page-body">
            <div class="container-xl">

                <div class="row g-4">
                    <div class="col-3">
                        <form action="indexAction" method="get">

                            <div class="subheader mb-2"><fmt:message key="index.check-in-date"/></div>
                            <div class="input-icon mb-2">
                                <input class="form-control" name="checkin-date" placeholder="<fmt:message key="index.select-date"/>" id="datepicker-start" value="${param['checkin-date']}" required>
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


                            <div class="subheader mb-2"><fmt:message key="index.check-out-date"/></div>
                            <div class="input-icon mb-2">
                                <input class="form-control" name="checkout-date"  placeholder="<fmt:message key="index.select-date"/>" id="datepicker-end" value="${param['checkout-date']}" required>
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


                            <div class="subheader mb-2"><fmt:message key="index.number-of-persons"/></div>
                            <div class="mb-3">
                                <input type="number" name="persons" class="quantity form-control" name="example-text-input"
                                       placeholder="" min="1" name="quantity" value="${param['persons'] == null ? 1 : param['persons']}" required>
                            </div>

                            <%--<div class="subheader mb-2"><fmt:message key="index.number-of-rooms"/></div>
                            <div class="mb-3">
                                <input type="number" name="rooms" class="quantity form-control" name="example-text-input"
                                       placeholder="" min="1" name="quantity" value="${param['rooms'] == null ? 1 : param['rooms']}" required>
                            </div>--%>

                            <div class="subheader mb-2"><fmt:message key="index.room-category"/></div>
                            <div class="mb-3">



                                <c:forEach items="${roomCategories}" var="roomCategory" varStatus="roomCategoryStatus">

                                    <label class="form-check">
                                        <input class="form-check-input" type="checkbox" name="room-category" value="${roomCategory.getId()}"
                                        <c:forEach items="${paramValues['room-category']}" var="roomCategoryParam">
                                            <c:if test="${roomCategoryParam == roomCategory.getId()}"> checked="checked"</c:if>
                                        </c:forEach>
                                        >
                                        <span class="form-check-label">${roomCategory.getName()}</span>
                                        <span class="form-check-description">${roomCategory.getDescription()}</span>
                                    </label>

                                </c:forEach>

                            </div>

                            <div class="subheader mb-2"><fmt:message key="index.price-per-night-room"/></div>
                            <div class="row g-2 align-items-center mb-3">
                                <div class="col">
                                    <div class="input-group">
                        <span class="input-group-text">$</span>
                                        <input type="text" name="price-from" class="form-control" placeholder="<fmt:message key="index.from"/>" value="${param['price-from']}"
                                               autocomplete="off">
                                    </div>
                                </div>
                                <div class="col-auto">—</div>
                                <div class="col">
                                    <div class="input-group">
                        <span class="input-group-text">$</span>
                                        <input type="text" name="price-to" class="form-control" placeholder="<fmt:message key="index.to"/>" value="${param['price-to']}" autocomplete="off">
                                    </div>
                                </div>
                            </div>

                            <div class="subheader mb-2"><fmt:message key="index.results-on-page"/></div>
                            <div class="mb-3">
                                    <select name="showBy" class="form-select" >
                                        <option value="5"<c:if test="${param['showBy'] == 5}"> selected</c:if>><fmt:message key="index.show-5"/></option>
                                        <option value="10"<c:if test="${param['showBy'] == 10}"> selected</c:if>><fmt:message key="index.show-10"/></option>
                                        <option value="20"<c:if test="${param['showBy'] == 20}"> selected</c:if>><fmt:message key="index.show-20"/></option>
                                    </select>
                            </div>


                            <div class="mt-3">
                                <button class="btn btn-primary w-100">
                                    <fmt:message key="index.confirm-changes"/>
                                </button>
                                <%--<a href="#" class="btn btn-link w-100">
                                    <fmt:message key="index.reset-to-defaults"/>
                                </a>--%>
                            </div>
                        </form>
                    </div>
                    <div class="col-9">


                        <div class="page-header d-print-none mb-2<c:if test="${rooms.size() == 0}"> visually-hidden</c:if>">
                            <div class="row g-2 align-items-center">
                                <div class="col">
                                    <h2 class="page-title">
                                        <fmt:message key="index.search-results"/>
                                    </h2>
                                    <div class="text-muted mt-1"><fmt:message key="index.showed"/> ${(page-1) * showBy + 1}-${(page * showBy) > resultSize ? resultSize : page * showBy} <fmt:message key="index.of"/> ${resultSize} <fmt:message key="index.rooms"/></div>
                                </div>

                            </div>
                        </div>

                        <div class="row row-cards">

                            <c:forEach items="${rooms}" var="room" varStatus="roomStatus">

                                <div class="card">

                                    <div class="ribbon bg-yellow"><fmt:message
                                            key="index.number"/>&nbsp;<b>${room.getNumber()}</b></div>

                                    <div class="row row-0">
                                        <div class="col-3">


                                            <div id="carousel-controls${roomStatus.count}" class="carousel slide"
                                                 data-bs-ride="carousel">
                                                <div class="carousel-inner">
                                                    <c:forEach items="${room.getImages()}" var="image"
                                                               varStatus="imageStatus">
                                                        <div class="carousel-item<c:if test="${imageStatus.count == 1}"> active</c:if>">
                                                            <img class="d-block w-100 h-100" alt="" src="${image}">
                                                        </div>
                                                    </c:forEach>

                                                </div>
                                                <a class="carousel-control-prev"
                                                   href="#carousel-controls${roomStatus.count}" role="button"
                                                   data-bs-slide="prev">
                                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                                    <span class="visually-hidden"><fmt:message
                                                            key="index.previous"/></span>
                                                </a>
                                                <a class="carousel-control-next"
                                                   href="#carousel-controls${roomStatus.count}" role="button"
                                                   data-bs-slide="next">
                                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                                    <span class="visually-hidden"><fmt:message key="index.next"/></span>
                                                </a>
                                            </div>

                                        </div>
                                        <div class="col">
                                            <div class="card-body">
                                                <h3 class="card-title">${room.getName()}</h3>
                                                <p class="text-muted">${room.getDescription()}</p>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="card-footer">
                                        <div class="d-flex">
                                            <h3 class="">• <fmt:message
                                                    key="index.occupancy"/>: ${room.getOccupancy()}
                                                • ${room.getRoomCategory().getName()} •</h3>
                                            <a href="#" class="btn btn-primary ms-auto"><fmt:message
                                                    key="index.book-for"/> ${room.getPrice()}$</a>
                                        </div>
                                    </div>

                                </div>
                            </c:forEach>

                            <div class="card<c:if test="${rooms.size() == 0}"> visually-hidden</c:if>">
                                <div class="card-body">
                                    <div class="d-flex">
                                        <ul class="pagination ">
                                            <li class="page-item disabled">
                                                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                                                    <!-- Download SVG icon from http://tabler-icons.io/i/chevron-left -->
                                                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><polyline points="15 6 9 12 15 18"></polyline></svg>
                                                </a>
                                            </li>
                                            <c:forEach var = "i" begin = "1" end = "${pages}">
                                                <li class="page-item<c:if test="${page == i}"> active</c:if>"><a class="page-link" href="pageAction?page=${i}"><c:out value = "${i}"/></a></li>
                                            </c:forEach>

                                            <li class="page-item">
                                                <a class="page-link" href="#">
                                                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><polyline points="9 6 15 12 9 18"></polyline></svg>
                                                </a>
                                            </li>
                                        </ul>

                                    </div>
                                </div>
                            </div>

                            <div class="card<c:if test="${rooms.size() > 0}"> visually-hidden</c:if>" >
                                <div class="empty">
                                    <div class="empty-img"><img src="./static/undraw_quitting_time_dm8t.svg" height="128" alt="">
                                    </div>
                                    <p class="empty-title"><fmt:message key="index.no-results-found"/></p>
                                    <p class="empty-subtitle text-muted">
                                        <fmt:message key="index.try-adjusting-your-search"/>
                                    </p>
                                    <div class="empty-action">

                                    </div>
                                </div>
                            </div>

                        </div>

                    </div>
                </div>
            </div>
        </div>

        <!-- Including Page footer -->
        <jsp:include page="footer.jsp"/>

    </div>
</div>
<!-- Libs JS -->
<script src="./dist/js/litepicker.js" defer></script>
<!-- Tabler Core -->
<script src="./dist/js/tabler.min.js" defer></script>
<script src="./dist/js/demo.min.js" defer></script>

<script>
    // @formatter:off
    document.addEventListener("DOMContentLoaded", function () {
        window.Litepicker && (new Litepicker({
            element: document.getElementById('datepicker-start'),
            elementEnd: document.getElementById('datepicker-end'),
            singleMode: false,
            tooltipText: {
                one: '<fmt:message key="index.night"/>',
                two: '<fmt:message key="index.night234"/>',
                three: '<fmt:message key="index.night234"/>',
                four: '<fmt:message key="index.night234"/>',
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
    // @formatter:on
</script>

</body>
</html>