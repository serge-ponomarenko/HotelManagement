<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var = "curLocale" value = "${param.locale == null ? 'en' : param.locale}"/>

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
            <a href="." class="navbar-brand navbar-brand-autodark"><img src="static/logo.svg" height="36" alt=""></a>
        </div>
        <form class="card card-md" action="signUpAction" method="post">
            <div class="card-body">
                <h2 class="card-title text-center mb-4"><fmt:message key="sign-up.create-new-account"/></h2>
                <c:if test="${param.msg == 'userAlreadyRegistered'}">
                    <h3 class="card-title text-center bg-red-lt mb-4"><fmt:message
                            key="sign-up.email-already-registered"/></h3>
                </c:if>
                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-up.select-language"/></label>

                    <select type="text" class="form-select" id="select-countries" name="locale" value=""
                            onchange="window.location.href = window.location.pathname + '?locale=' + this.options[this.selectedIndex].value">

                        <c:forEach items="${locales.values()}" var="locale">
                        <option value="${locale.getName()}"
                                data-custom-properties="&lt;span class=&quot;flag flag-xs ${locale.getIconPath()}&quot;&gt;&lt;/span&gt;"
                                <c:if test="${param.locale == locale.getName()}">
                                    selected
                                </c:if>
                        >
                                ${locale.getFullName()}
                        </option>
                        </c:forEach>

                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-up.first-name"/></label>
                    <input type="text" name="firstName" class="form-control"
                           placeholder="<fmt:message key="sign-up.enter-first-name"/>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-up.last-name"/></label>
                    <input type="text" name="lastName" class="form-control"
                           placeholder="<fmt:message key="sign-up.enter-last-name"/>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-up.email"/></label>
                    <input type="email" name="email" class="form-control"
                           placeholder="<fmt:message key="sign-up.enter-email"/>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"><fmt:message key="sign-up.password"/></label>
                    <div class="input-group input-group-flat">
                        <input type="password" name="password" class="form-control"
                               placeholder="<fmt:message key="sign-up.enter-password"/>" autocomplete="off" required>
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
                <div class="mb-3">
                    <label class="form-check">
                        <input type="checkbox" class="form-check-input" required/>
                        <span class="form-check-label"><fmt:message key="sign-up.agree-the"/> <a
                                href="./terms-of-service.html" tabindex="-1"><fmt:message
                                key="sign-up.terms-and-policy"/></a>.</span>
                    </label>
                </div>
                <div class="form-footer">
                    <button type="submit" class="btn btn-primary w-100"><fmt:message
                            key="sign-up.create-new-account-button"/></button>
                </div>
            </div>
        </form>
        <div class="text-center text-muted mt-3">
            <fmt:message key="sign-up.already-have-account"/> <a
                href="signInAction?locale=${curLocale}" tabindex="-1"><fmt:message
                key="sign-up.sign-in"/></a>
        </div>
    </div>
</div>
<!-- Libs JS -->
<script src="./dist/js/tom-select.base.min.js" defer></script>
<!-- Tabler Core -->
<script src="./dist/js/tabler.min.js" defer></script>
<script src="./dist/js/demo.min.js" defer></script>
<script>
    // @formatter:off
    document.addEventListener("DOMContentLoaded", function () {
        var el;
        window.TomSelect && (new TomSelect(el = document.getElementById('select-countries'), {
            copyClassesToDropdown: false,
            dropdownClass: 'dropdown-menu ts-dropdown',
            optionClass: 'dropdown-item',
            controlInput: '<input>',
            render: {
                item: function (data, escape) {
                    if (data.customProperties) {
                        return '<div><span class="dropdown-item-indicator">' + data.customProperties + '</span>' + escape(data.text) + '</div>';
                    }
                    return '<div>' + escape(data.text) + '</div>';
                },
                option: function (data, escape) {
                    if (data.customProperties) {
                        return '<div><span class="dropdown-item-indicator">' + data.customProperties + '</span>' + escape(data.text) + '</div>';
                    }
                    return '<div>' + escape(data.text) + '</div>';
                },
            },
        }));
    });
    // @formatter:on
</script>
</body>
</html>