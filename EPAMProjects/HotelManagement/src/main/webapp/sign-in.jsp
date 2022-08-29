<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${param.locale == null ? 'en' : param.locale}"/>
<fmt:setBundle basename="Strings"/>

<!doctype html>
<html lang="${param.locale == null ? 'en' : param.locale}">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"/>
    <title><fmt:message key="sign-in.page-title"/></title>
    <!-- CSS files -->
    <link href="./dist/css/tabler.min.css" rel="stylesheet"/>
    <link href="./dist/css/tabler-flags.min.css" rel="stylesheet"/>
    <link href="./dist/css/tabler-payments.min.css" rel="stylesheet"/>
    <link href="./dist/css/tabler-vendors.min.css" rel="stylesheet"/>
    <link href="./dist/css/demo.min.css" rel="stylesheet"/>
</head>
<body class=" border-top-wide border-primary d-flex flex-column">
<div class="page page-center">
    <div class="container-tight py-4">
        <div class="text-center mb-4">
            <a href="." class="navbar-brand navbar-brand-autodark"><img src="./static/logo.svg" height="36" alt=""></a>
        </div>
        <form class="card card-md" action="/signInAction" method="post" autocomplete="off">
            <div class="card-body">
                <div class="row g-2 align-items-center">
                    <div class="col col-md-auto ms-auto">
                        <h2 class="text-center"><fmt:message key="sign-in.login-to-your-account"/></h2>
                    </div>
                    <!-- Page title actions -->
                    <div class="col-12 col-md-auto ms-auto d-print-none">
                        <a href="?locale=en" class="d-none d-sm-inline-block">
                            <span class="flag flag-country-gb"></span>
                        </a>
                        <a href="?locale=uk" class="d-none d-sm-inline-block">
                            <span class="flag flag-country-ua"></span>
                        </a>
                    </div>
                </div>
                <c:if test="${param.msg == 'noUserFound'}">
                    <h3 class="card-title text-center bg-red-lt mb-4"><fmt:message
                            key="sign-in.user-is-not-registered"/></h3>
                </c:if>
                <c:if test="${param.msg == 'wrongPassword'}">
                    <h3 class="card-title text-center bg-red-lt mb-4"><fmt:message
                            key="sign-in.password-incorrect"/></h3>
                </c:if>
                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-in.email-address"/></label>
                    <input type="email" name="email" class="form-control"
                           placeholder="<fmt:message key="sign-in.enter-email"/>" autocomplete="off"
                    <c:if test="${param.msg == 'wrongPassword'}">
                           value="${param.email}"
                    </c:if>
                           required>
                </div>
                <div class="mb-2">
                    <label class="form-label">
                        <fmt:message key="sign-in.password"/>
                        <span class="form-label-description">
                  <a href="./forgot-password.html"><fmt:message key="sign-in.forgot-password"/></a>
                </span>
                    </label>
                    <div class="input-group input-group-flat">
                        <input type="password" name="password" class="form-control"
                               placeholder="<fmt:message key="sign-in.enter-password"/>"
                               autocomplete="off" required>
                        <span class="input-group-text">
                  <a href="#" class="link-secondary" title="Show password" data-bs-toggle="tooltip">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round"
                         stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><circle cx="12"
                                                                                                            cy="12"
                                                                                                            r="2"/><path
                            d="M22 12c-2.667 4.667 -6 7 -10 7s-7.333 -2.333 -10 -7c2.667 -4.667 6 -7 10 -7s7.333 2.333 10 7"/></svg>
                  </a>
                </span>
                    </div>
                </div>
                <div class="mb-2">
                    <label class="form-check">
                        <input type="checkbox" name="remember" class="form-check-input"/>
                        <span class="form-check-label"><fmt:message key="sign-in.remember-me"/></span>
                    </label>
                </div>
                <div class="form-footer">
                    <button type="submit" class="btn btn-primary w-100"><fmt:message key="sign-in.sign-in"/></button>
                </div>
            </div>
        </form>
        <div class="text-center text-muted mt-3">
            <fmt:message key="sign-in.dont-have-account"/> <a
                href="./sign-up.jsp?locale=${param.locale == null ? 'en' : param.locale}" tabindex="-1"><fmt:message
                key="sign-in.sign-up"/></a>
        </div>
    </div>
</div>
<!-- Libs JS -->
<!-- Tabler Core -->
<script src="./dist/js/tabler.min.js" defer></script>
<script src="./dist/js/demo.min.js" defer></script>
</body>
</html>