package ua.cc.spon.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.UserSettings;

import java.util.List;

public class PendingRequestBadgeTagHandler extends TagSupport {

    public int doStartTag() {

        ServletContext context = pageContext.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) pageContext.getSession().getAttribute("userSettings")).getLocale();

        List<Request> requests = requestDAO.findAllPending(locale);

        if (requests != null && !requests.isEmpty()) {
            int count = requests.size();

                JspWriter out = pageContext.getOut();
                try {
                    out.print("&nbsp;<span class=\"badge badge-sm bg-red\">" + count + "</span>");
                } catch (Exception ignore) { }

        }

        return SKIP_BODY;
    }

}