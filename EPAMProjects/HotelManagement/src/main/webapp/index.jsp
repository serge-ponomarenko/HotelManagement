<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="myTags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<c:if test="${rooms == null}"><jsp:forward page="indexAction" /></c:if>

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
                                <input class="form-control" name="checkin-date"
                                       placeholder="<fmt:message key="index.select-date"/>" id="datepicker-start"
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


                            <div class="subheader mb-2"><fmt:message key="index.check-out-date"/></div>
                            <div class="input-icon mb-2">
                                <input class="form-control" name="checkout-date"
                                       placeholder="<fmt:message key="index.select-date"/>" id="datepicker-end"
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


                            <div class="subheader mb-2"><fmt:message key="index.number-of-persons"/></div>
                            <div class="mb-3">
                                <input type="number" name="persons" class="quantity form-control"
                                       name="example-text-input"
                                       placeholder="" min="1" name="quantity"
                                       value="${param['persons'] == null ? 1 : param['persons']}" required>
                            </div>

                             <div class="subheader mb-2"><fmt:message key="index.room-category"/></div>
                            <div class="mb-3">


                                <c:forEach items="${roomCategories}" var="roomCategory" varStatus="roomCategoryStatus">

                                    <label class="form-check">
                                        <input class="form-check-input" type="checkbox" name="room-category"
                                               value="${roomCategory.getId()}"
                                        <c:forEach items="${paramValues['room-category']}" var="roomCategoryParam">
                                        <c:if test="${roomCategoryParam == roomCategory.getId()}">
                                               checked="checked"</c:if>
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
                                        <input type="number" name="price-from" class="form-control"
                                               placeholder="<fmt:message key="index.from"/>"
                                               value="${param['price-from']}"
                                               autocomplete="off">
                                    </div>
                                </div>
                                <div class="col-auto">—</div>
                                <div class="col">
                                    <div class="input-group">
                                        <span class="input-group-text">$</span>
                                        <input type="number" name="price-to" class="form-control"
                                               placeholder="<fmt:message key="index.to"/>" value="${param['price-to']}"
                                               autocomplete="off">
                                    </div>
                                </div>
                            </div>

                            <div class="mt-3">
                                <button class="btn btn-primary w-100">
                                    <fmt:message key="index.confirm-changes"/>
                                </button>
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
                                    <div class="text-muted mt-1"><fmt:message
                                            key="index.showed"/> ${paginator.getShowedStart()}-${paginator.getShowedEnd()}
                                        <fmt:message key="index.of"/> ${paginator.getResultSize()} <fmt:message
                                                key="index.rooms"/></div>
                                </div>
                                <div class="col-2 ">
                                    <select name="sortBy" class="form-select" onchange="window.location.href = 'pageAction?sortBy-${paginator.getPageName()}=' + this.options[this.selectedIndex].value">
                                        <option value="price"<c:if test="${paginator.getSortBy().equals('price')}"> selected</c:if>>
                                            <fmt:message key="index.sortby-price"/></option>
                                        <option value="category"<c:if test="${paginator.getSortBy().equals('category')}"> selected</c:if>>
                                            <fmt:message key="index.sortby-category"/></option>
                                        <option value="occupancy"<c:if test="${paginator.getSortBy().equals('occupancy')}"> selected</c:if>>
                                            <fmt:message key="index.sortby-occupancy"/></option>
                                    </select>
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
                                            <div class="card-actions">
                                                <form action="bookAction" method="post">
                                                    <input type="hidden" name="room" value="${room.getId()}">
                                                    <input type="hidden" name="checkin-date" value="${param['checkin-date']}">
                                                    <input type="hidden" name="checkout-date" value="${param['checkout-date']}">
                                                    <input type="hidden" name="persons" value="${param['persons']}">
                                                <button class="btn btn-primary mt-1"><fmt:message key="index.book-for"/> ${nights} <fmt:message key="index.nights-for"/> ${room.getPrice() * nights}$</button>
                                                </form>
                                            </div>

                                        </div>
                                    </div>

                                </div>
                            </c:forEach>

                            <div class="card<c:if test="${rooms.size() == 0}"> visually-hidden</c:if>">
                                <div class="card-body">
                                <!-- Paginator -->
                                <jsp:include page="fragments/paginator.jsp"/>
                                </div>
                            </div>

                            <div class="card<c:if test="${rooms.size() > 0}"> visually-hidden</c:if>">
                                <div class="empty">
                                    <div class="empty-img"><img src="./static/undraw_quitting_time_dm8t.svg"
                                                                height="128" alt="">
                                    </div>
                                    <p class="empty-title"><fmt:message key="index.no-results-found"/></p>
                                    <p class="empty-subtitle text-muted">
                                        <fmt:message key="index.try-adjusting-your-search"/>
                                    </p>
                                    <div class="empty-action">
                                        <a href="./." class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modal-report">
                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                 class="icon icon-tabler icon-tabler-user-search" width="24" height="24"
                                                 viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none"
                                                 stroke-linecap="round" stroke-linejoin="round">
                                                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                                <circle cx="12" cy="7" r="4"></circle>
                                                <path d="M6 21v-2a4 4 0 0 1 4 -4h1"></path>
                                                <circle cx="16.5" cy="17.5" r="2.5"></circle>
                                                <path d="M18.5 19.5l2.5 2.5"></path>
                                            </svg>
                                            <fmt:message key="index.or-make-a-request"/>
                                        </a>
                                    </div>
                                </div>

                            </div>

                        </div>
                    </div>
                </div>
            </div>

            <!-- Including Page footer -->
            <jsp:include page="fragments/footer.jsp"/>

            <div class="modal modal-blur fade" id="modal-report" tabindex="-1" style="display: none;" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <form action="requestAction" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title"><fmt:message key="index.reservation-request"/></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>

                        <div class="modal-body">

                            <div class="row">
                                <div class="col-lg-6">
                                    <div class="mb-3">
                                        <label class="form-label"><fmt:message key="index.check-in-date"/></label>
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
                                        <label class="form-label"><fmt:message key="index.check-out-date"/></label>
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

                            <div class="row">
                                <div class="col-lg-6">
                                    <label class="form-label"><fmt:message key="index.number-of-persons"/></label>
                                    <div class="mb-3">
                                        <input type="number" name="persons" class="quantity form-control"
                                               name="example-text-input"
                                               placeholder="" min="1" name="quantity"
                                               value="1" required>
                                    </div>
                                </div>
                                <div class="col-lg-6">
                                    <label class="form-label"><fmt:message key="index.number-of-rooms"/></label>
                                    <div class="mb-3">
                                        <input type="number" name="rooms" class="quantity form-control"
                                               name="example-text-input"
                                               placeholder="" min="1" name="quantity"
                                               value="1" required>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-12">
                                <label class="form-label"><fmt:message key="index.room-category"/></label>
                                <div class="mb-3">

                                    <c:forEach items="${roomCategories}" var="roomCategory" varStatus="roomCategoryStatus">

                                        <label class="form-check">
                                            <input class="form-check-input" type="checkbox" name="room-category"
                                                   value="${roomCategory.getId()}">
                                            <span class="form-check-label">${roomCategory.getName()}</span>
                                            <span class="form-check-description">${roomCategory.getDescription()}</span>
                                        </label>

                                    </c:forEach>

                                </div>
                            </div>
                            <div class="col-lg-12">
                                <div>
                                    <label class="form-label"><fmt:message key="index.additional-information"/></label>
                                    <textarea class="form-control" name="additional-information" rows="2" spellcheck="false"></textarea>
                                </div>
                            </div>

                        </div>
                        <div class="modal-footer">
                            <a href="#" class="btn btn-link link-secondary" data-bs-dismiss="modal">
                                <fmt:message key="index.cancel"/>
                            </a>
                            <button type="submit" class="btn btn-primary ms-auto">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"></path><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                                <fmt:message key="index.send-request"/>
                            </button>
                        </div>
                        </form>
                    </div>

                </div>
            </div>

            <myTags:success_message message="${success_message}" />
            <myTags:fail_message message="${fail_message}" />

        </div>
    </div>
    <!-- Libs JS -->
    <script src="./dist/js/litepicker.js" defer></script>

    <!-- Tabler Core -->
    <script src="./dist/js/tabler.min.js" defer></script>
    <script src="./dist/js/demo.min.js" defer></script>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            window.Litepicker && (new Litepicker({
                element: document.getElementById('datepicker-start'),
                elementEnd: document.getElementById('datepicker-end'),
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