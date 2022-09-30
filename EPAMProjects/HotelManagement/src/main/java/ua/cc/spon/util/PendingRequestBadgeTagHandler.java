package ua.cc.spon.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;

import java.util.List;

public class PendingRequestBadgeTagHandler extends TagSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingRequestBadgeTagHandler.class);

    @Override
    public int doStartTag() {
        ServletContext context = pageContext.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) pageContext.getSession().getAttribute("userSettings")).getLocale();

        transaction.init(requestDAO);

        List<Request> requests = null;
        try {
            requests = requestDAO.findAllPending(locale);
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            transaction.end();
        }

        if (requests != null && !requests.isEmpty()) {
            int count = requests.size();

                JspWriter out = pageContext.getOut();
                try {
                    out.print("&nbsp;<span class=\"badge badge-sm bg-red\">" + count + "</span>");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
        }

        return SKIP_BODY;
    }

}