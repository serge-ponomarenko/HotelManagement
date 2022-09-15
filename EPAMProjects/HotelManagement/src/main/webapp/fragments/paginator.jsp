<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.userSettings.getLocale()}"/>
<fmt:setBundle basename="Strings"/>

    <div class="d-flex">
        <ul class="pagination m-0">
            <li class="page-item<c:if test="${paginator.getCurrentPage() == 1}"> disabled</c:if>">
                <a class="page-link" href="pageAction?page=${paginator.getCurrentPage()-1}" tabindex="-1" aria-disabled="true">
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
            <li class="page-item<c:if test="${paginator.getCurrentPage() == 1}"> active</c:if>"><a
                    class="page-link" href="pageAction?page=1">1</a></li>

            <c:if test="${paginator.getPages() > 1 && paginator.getPages() <= 10}">
                <c:forEach var="i" begin="2" end="${paginator.getPages()}">
                    <li class="page-item<c:if test="${paginator.getCurrentPage() == i}"> active</c:if>"><a
                            class="page-link" href="pageAction?page=${i}"><c:out
                            value="${i}"/></a></li>
                </c:forEach>
            </c:if>

            <c:if test="${paginator.getPages() > 10 && paginator.getCurrentPage() <=6}">
                <c:forEach var="i" begin="2" end="9">
                    <li class="page-item<c:if test="${paginator.getCurrentPage() == i}"> active</c:if>"><a
                            class="page-link" href="pageAction?page=${i}"><c:out
                            value="${i}"/></a></li>
                </c:forEach>
                <li class="page-item">...</li>
            </c:if>

            <c:if test="${paginator.getPages() > 10 && paginator.getCurrentPage() > 6 && paginator.getCurrentPage() < paginator.getPages() - 5}">
                <li class="page-item">...</li>
                <c:forEach var="i" begin="${paginator.getCurrentPage() - 4}" end="${paginator.getCurrentPage() + 4}">
                    <li class="page-item<c:if test="${paginator.getCurrentPage() == i}"> active</c:if>"><a
                            class="page-link" href="pageAction?page=${i}"><c:out
                            value="${i}"/></a></li>
                </c:forEach>
                <li class="page-item">...</li>
            </c:if>

            <c:if test="${paginator.getPages() > 10 && paginator.getCurrentPage() > 6 && paginator.getCurrentPage() >= paginator.getPages() - 5}">
                <li class="page-item">...</li>
                <c:forEach var="i" begin="${paginator.getPages() - 9}" end="${paginator.getPages() - 1}">
                    <li class="page-item<c:if test="${paginator.getCurrentPage() == i}"> active</c:if>"><a
                            class="page-link" href="pageAction?page=${i}"><c:out
                            value="${i}"/></a></li>
                </c:forEach>
            </c:if>

            <c:if test="${paginator.getPages() > 10}">
            <li class="page-item<c:if test="${paginator.getCurrentPage() == paginator.getPages()}"> active</c:if>"><a
                    class="page-link" href="pageAction?page=${paginator.getPages()}">${paginator.getPages()}</a></li>
            </c:if>

            <li class="page-item<c:if test="${paginator.getCurrentPage() == paginator.getPages()}"> disabled</c:if>">
                <a class="page-link" href="pageAction?page=${paginator.getCurrentPage()+1}" aria-disabled="true">
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
