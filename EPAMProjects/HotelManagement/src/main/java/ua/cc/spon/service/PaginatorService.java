package ua.cc.spon.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class PaginatorService {

    private final String pageName;
    private int currentPage;
    private int showBy;
    private String sortBy;
    private int resultSize;
    private int pages;
    private int showedStart;
    private int showedEnd;
    private final Integer[] defaultShowedItemsCount;

    private final HttpServletRequest req;

    public PaginatorService(HttpServletRequest req, String pageName, Integer[] defaultShowedItemsCount) {
        this.req = req;
        this.pageName = pageName;
        this.defaultShowedItemsCount = defaultShowedItemsCount;

        init();
    }

    private void init() {
        currentPage = (int) Optional
                .ofNullable(Optional
                        .ofNullable(req.getSession())
                        .orElseThrow()
                        .getAttribute("page")).orElse(1);
        req.getSession().removeAttribute("page");

        showBy = (int) Optional
                .ofNullable(Optional
                        .ofNullable(req.getSession())
                        .orElseThrow()
                        .getAttribute("showBy-" + pageName)).orElse(defaultShowedItemsCount[0]);

        sortBy = (String) Optional
                .ofNullable(Optional
                        .ofNullable(req.getSession())
                        .orElseThrow()
                        .getAttribute("sortBy-" + pageName)).orElse("");

    }

    public static void proceedParametersToSession(HttpServletRequest req) {
        Enumeration<String> parameterNames = req.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.equals("page")) {
                int page = Integer.parseInt(req.getParameter(parameterName));
                req.getSession().setAttribute(parameterName, page);
            }
            if (parameterName.startsWith("showBy-")) {
                int showBy = Integer.parseInt(req.getParameter(parameterName));
                req.getSession().setAttribute(parameterName, showBy);
            }
            if (parameterName.startsWith("sortBy-")) {
                String sortBy = req.getParameter(parameterName);
                req.getSession().setAttribute(parameterName, sortBy);
            }

        }
    }

    public <T> List<T> generateSublist(List<T> list) {
        resultSize = list.size();

        int showByCalc = Math.min(showBy, resultSize);

        int start = Math.min((currentPage-1) * showByCalc, resultSize);
        int end = Math.min(currentPage * showByCalc, resultSize);
        pages = (int) Math.ceil((double) resultSize / showByCalc);

        showedStart = (currentPage-1) * showBy + 1;
        showedEnd = Math.min((currentPage * showBy), resultSize);

        return list.subList(start, end);
    }

    public void setRequestAttributes() {
        req.setAttribute("paginator", this);
    }

    public String getPageName() {
        return pageName;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getShowBy() {
        return showBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public Integer[] getDefaultShowedItemsCount() {
        return defaultShowedItemsCount;
    }

    public int getResultSize() {
        return resultSize;
    }

    public int getPages() {
        return pages;
    }

    public int getShowedStart() {
        return showedStart;
    }

    public int getShowedEnd() {
        return showedEnd;
    }
}
