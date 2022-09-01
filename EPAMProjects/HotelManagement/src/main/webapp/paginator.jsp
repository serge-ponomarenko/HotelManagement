<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

<% if (request.getAttribute("rooms") == null) response.sendRedirect("indexAction"); %>

<div class="card-body">
    <div class="d-flex">
        <ul class="pagination">
            <li class="page-item<c:if test="${page == 1}"> disabled</c:if>">
                <a class="page-link" href="pageAction?page=${page-1}" tabindex="-1" aria-disabled="true">
                    <!-- Download SVG icon from http://tabler-icons.io/i/chevron-left -->
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24"
                         height="24" viewBox="0 0 24 24" stroke-width="2"
                         stroke="currentColor" fill="none" stroke-linecap="round"
                         stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <polyline points="15 6 9 12 15 18"></polyline>
                    </svg>
                </a>
            </li>
            <c:forEach var="i" begin="1" end="${pages}">
                <li class="page-item<c:if test="${page == i}"> active</c:if>"><a
                        class="page-link" href="pageAction?page=${i}"><c:out
                        value="${i}"/></a></li>
            </c:forEach>

            <li class="page-item<c:if test="${page == pages}"> disabled</c:if>">
                <a class="page-link" href="pageAction?page=${page+1}" aria-disabled="true">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24"
                         height="24" viewBox="0 0 24 24" stroke-width="2"
                         stroke="currentColor" fill="none" stroke-linecap="round"
                         stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <polyline points="9 6 15 12 9 18"></polyline>
                    </svg>
                </a>
            </li>
        </ul>

    </div>
</div>
