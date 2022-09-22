<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="curLocale" value="${param.locale == null ? 'en' : param.locale}"/>

<fmt:setLocale value="${curLocale}"/>
<fmt:setBundle basename="Strings"/>

<!doctype html>
<html lang="${curLocale}">
<head>
    <%@ include file="fragments/head.jsp" %>
</head>
<body class=" border-top-wide border-primary d-flex flex-column">
<div class="page page-center">
    <div class="container-tight py-4">
        <div class="text-center mb-4">
            <a href="." class="navbar-brand navbar-brand-autodark"><img src="./static/logo.svg" height="36" alt=""></a>
        </div>
        <form class="card card-md" action="signInAction" method="post" autocomplete="off">
            <div class="card-body">
                <div class="row g-2 align-items-center">
                    <div class="col col-md-auto ms-auto">
                        <h2 class="text-center"><fmt:message key="sign-in.login-to-your-account"/></h2>
                    </div>
                    <!-- Page title actions -->

                    <div class="col-12 col-md-auto ms-auto d-print-none">
                        <div class="nav-item dropdown">
                            <a href="#" class="nav-link d-flex lh-1 text-reset p-0" data-bs-toggle="dropdown"
                               aria-label="Language">
                                <span class="flag ${locales.get(curLocale).getIconPath()}"></span>
                            </a>
                            <div class="dropdown-menu dropdown-menu-end dropdown-menu-arrow">
                                <c:forEach items="${locales.values()}" var="locale">
                                    <a href="signInAction?locale=${locale.getName()}" class="dropdown-item"><span
                                            class="flag ${locale.getIconPath()}"></span>&nbsp;${locale.getFullName()}
                                    </a>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty param.msg}">
                    <h3 class="card-title text-center bg-red-lt mb-4"><fmt:message
                            key="error.${param.msg}"/></h3>
                </c:if>

                <div class="text-center bg-teal-lt col col-md-auto ms-auto">
                    Login as: <a href="#" id="ladmin">Admin</a> •
                    <a href="#" id="lmanager">Manager</a> •
                    <a href="#" id="luser1">User1</a> •
                    <a href="#" id="luser2">User2</a> •
                    <a href="#" id="luser3">User3</a> •
                </div>

                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-in.email-address"/></label>
                    <input type="email" id="email" name="email" class="form-control"
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
                        <input id="password" type="password" name="password" class="form-control"
                               placeholder="<fmt:message key="sign-in.enter-password"/>"
                               autocomplete="off" required>
                        <span class="input-group-text">
                  <a class="link-secondary" title="Show password" onclick="myFunction()" data-bs-toggle="tooltip">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><circle cx="12" cy="12" r="2"/><path d="M22 12c-2.667 4.667 -6 7 -10 7s-7.333 -2.333 -10 -7c2.667 -4.667 6 -7 10 -7s7.333 2.333 10 7"/></svg>
                  </a>
                </span>
                    </div>
                </div>
                <small class="form-hint mb-2"><fmt:message key="sing-in.minimum-eight-characters"/></small>

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
                href="signUpAction?locale=${curLocale}" tabindex="-1"><fmt:message
                key="sign-in.sign-up"/></a>
        </div>
    </div>
</div>
<!-- Libs JS -->
<!-- Tabler Core -->
<script src="dist/js/tabler.min.js" defer></script>
<script src="dist/js/demo.min.js" defer></script>
<script>
    function myFunction() {
        var x = document.getElementById("password");
        if (x.type === "password") {
            x.type = "text";
        } else {
            x.type = "password";
        }
    }
</script>
<script>
    $(function () {
        $('#ladmin').click(function () {
            $("#email").val("s.stefaniv@gmail.com");
            $("#password").val("qwertyuiop123");
        });
    });
    $(function () {
        $('#lmanager').click(function () {
            $("#email").val("gen.justice@gmail.com");
            $("#password").val("qwertyuiop123");
        });
    });
    $(function () {
        $('#luser1').click(function () {
            $("#email").val("corbett@gmail.com");
            $("#password").val("qwertyuiop123");
        });
    });
    $(function () {
        $('#luser2').click(function () {
            $("#email").val("f.seymour@gmail.com");
            $("#password").val("qwertyuiop123");
        });
    });
    $(function () {
        $('#luser3').click(function () {
            $("#email").val("a.simons@gmail.com");
            $("#password").val("qwertyuiop123");
        });
    });
</script>
</body>
</html>