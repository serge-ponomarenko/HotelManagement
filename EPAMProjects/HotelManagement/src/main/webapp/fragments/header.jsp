<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib uri="/WEB-INF/badge-tags.tld" prefix="bt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<header class="navbar navbar-expand-md navbar-light d-print-none">
    <div class="container-xl">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar-menu">
            <span class="navbar-toggler-icon"></span>
        </button>
        <h1 class="navbar-brand navbar-brand-autodark d-none-navbar-horizontal pe-0 pe-md-3">
            <a href=".">
                <img src="static/logo.svg" width="110" height="32" alt="Hotel" class="navbar-brand-image">
            </a>
        </h1>

        <div class="collapse navbar-collapse order-md-last" id="navbar-menu">

        </div>
        <div class="navbar-nav flex-row order-md-last">
            <div class="d-flex flex-column flex-md-row flex-fill align-items-stretch align-items-md-center me-3">
                <ul class="navbar-nav">
                    <c:if test="${user.getRole().toString().equals('ADMINISTRATOR') or user.getRole().toString().equals('MANAGER')}">
                    <li class="nav-item active dropdown">
                        <a class="nav-link dropdown-toggle" href="#navbar-layout" data-bs-toggle="dropdown" data-bs-auto-close="outside" role="button" aria-expanded="false">
                    <span class="nav-link-icon d-md-none d-lg-inline-block"><!-- Download SVG icon from http://tabler-icons.io/i/layout-2 -->
                      <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24"
                           viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none"
                           stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <rect x="5" y="5" width="14" height="14" rx="1"></rect>
                            <path d="M8 10v-2h2m6 6v2h-2m-4 0h-2v-2m8 -4v-2h-2"></path>
                            <path d="M3 10h2"></path>
                            <path d="M3 14h2"></path>
                            <path d="M10 3v2"></path>
                            <path d="M14 3v2"></path>
                            <path d="M21 10h-2"></path>
                            <path d="M21 14h-2"></path>
                            <path d="M14 21v-2"></path>
                            <path d="M10 21v-2"></path>
                        </svg>
                    </span>
                            <span class="nav-link-title">
                      <fmt:message key="header.admin-menu"/>
                    </span>
                        </a>
                        <div class="dropdown-menu">
                            <div class="dropdown-menu-columns">
                                <div class="dropdown-menu-column">
                                    <a class="dropdown-item" href="hotelOccupancyAction">
                                        <fmt:message key="header.hotel-occupancy"/>
                                    </a>
                                    <a class="dropdown-item" href="reservationRequestsAction">
                                        <fmt:message key="header.pending-reservation-requests"/>
                                        <bt:pendingRequestsBadgeTag />
                                    </a>
                                    <a class="dropdown-item" href="allBookingsAction">
                                        <fmt:message key="header.manage-reservations"/>
                                    </a>

                                    <c:if test="${user.getRole().toString().equals('ADMINISTRATOR')}">
                                    <div class="dropdown-divider"></div>
                                    <a class="dropdown-item" href="manageUsersAction">
                                        <fmt:message key="header.manage-users"/>
                                    </a>
                                    <a class="dropdown-item" href="manageCategoriesAction">
                                        <fmt:message key="header.manage-categories"/>
                                    </a>
                                    <a class="dropdown-item" href="manageRoomsAction">
                                        <fmt:message key="header.manage-rooms"/>
                                    </a>
                                    </c:if>

                                </div>
                            </div>
                        </div>
                    </li>
                    </c:if>

                    <li class="nav-item">
                        <a class="nav-link" href="myBookingsAction">
                    <span class="nav-link-icon d-md-none d-lg-inline-block"><!-- Download SVG icon from http://tabler-icons.io/i/ghost -->
                      <svg xmlns="http://www.w3.org/2000/svg" class="icon text-pink" width="24" height="24"
                           viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none"
                           stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <path d="M3 21v-13l9 -4l9 4v13"></path>
                            <path d="M13 13h4v8h-10v-6h6"></path>
                            <path d="M13 21v-9a1 1 0 0 0 -1 -1h-2a1 1 0 0 0 -1 1v3"></path>
                        </svg>
                    </span>
                            <span class="nav-link-title">
                      <fmt:message key="header.my-bookings"/>
                    </span>
                            <bt:bookingBadgeTag />
                        </a>
                    </li>
                </ul>
            </div>


            <div class="nav-item dropdown">
                <a href="#" class="nav-link d-flex lh-1 text-reset p-0" data-bs-toggle="dropdown"
                   aria-label="Language">
                            <span class="flag ${locales.get(sessionScope.userSettings.getLocale()).getIconPath()}"></span>
                </a>

                <div class="dropdown-menu dropdown-menu-end dropdown-menu-arrow">
                    <c:forEach items="${locales.values()}" var="locale">
                        <a href="localeAction?locale=${locale.getName()}" class="dropdown-item"><span class="flag ${locale.getIconPath()}"></span>&nbsp;${locale.getFullName()}</a>
                    </c:forEach>
                </div>
            </div>

            <div class="d-none d-md-flex me-3">
                <a href="?theme=dark" class="nav-link px-0 hide-theme-dark" title="<fmt:message key="header.dark-mode"/>"
                   data-bs-toggle="tooltip" data-bs-placement="bottom">

                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round"
                         stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <path d="M12 3c.132 0 .263 0 .393 0a7.5 7.5 0 0 0 7.92 12.446a9 9 0 1 1 -8.313 -12.454z"/>
                    </svg>
                </a>
                <a href="?theme=light" class="nav-link px-0 hide-theme-light" title="<fmt:message key="header.light-mode"/>"
                   data-bs-toggle="tooltip" data-bs-placement="bottom">

                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round"
                         stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <circle cx="12" cy="12" r="4"/>
                        <path d="M3 12h1m8 -9v1m8 8h1m-9 8v1m-6.4 -15.4l.7 .7m12.1 -.7l-.7 .7m0 11.4l.7 .7m-12.1 -.7l-.7 .7"/>
                    </svg>
                </a>
            </div>


            <div class="nav-item dropdown">
                <a href="#" class="nav-link d-flex lh-1 text-reset p-0" data-bs-toggle="dropdown"
                   aria-label="Open user menu">
                    <span class="avatar avatar-sm" style="background-image: url(static/000.webp)"></span>
                    <div class="d-none d-xl-block ps-2">
                        <div>${sessionScope.user.getFirstName()} ${sessionScope.user.getLastName()}</div>
                        <div class="mt-1 small text-muted">${sessionScope.user.getRole().toString().substring(0,1).toUpperCase()}${sessionScope.user.getRole().toString().substring(1).toLowerCase()}</div>
                    </div>
                </a>
                <div class="dropdown-menu dropdown-menu-end dropdown-menu-arrow">
                    <a href="editUserAction?user_id=${user.getId()}" class="dropdown-item"><fmt:message key="header.profile-account"/></a>
                    <a href="myBookingsAction" class="dropdown-item"><fmt:message key="header.my-bookings"/></a>
                    <div class="dropdown-divider"></div>
                    <a href="#" class="dropdown-item"><fmt:message key="header.settings"/></a>
                    <a href="logoutAction" class="dropdown-item"><fmt:message key="header.logout"/></a>
                </div>
            </div>
        </div>

    </div>
</header>