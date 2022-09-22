<%@ attribute name="field_name" required="true" type="java.lang.String" description="Field name." %>
<%@ attribute name="paginator" required="true" type="ua.cc.spon.service.PaginatorService" description="Paginator object." %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var = "t_asc" value = "${field_name}-asc"/>
<c:set var = "t_desc" value = "${field_name}-desc"/>
<c:set var = "t_" value = "${field_name}-"/>

<c:if test='${paginator.getSortBy().equals(t_asc)}'> href="pageAction?sortBy-${paginator.getPageName()}=${t_desc}" class="table-sort asc" </c:if>
<c:if test="${paginator.getSortBy().equals(t_desc)}"> href="pageAction?sortBy-${paginator.getPageName()}=${t_asc}" class="table-sort desc" </c:if>
<c:if test="${not paginator.getSortBy().startsWith(t_)}"> href="pageAction?sortBy-${paginator.getPageName()}=${t_asc}" class="table-sort" </c:if>