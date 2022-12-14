package ua.cc.spon.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.entity.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static ua.cc.spon.db.entity.User.Role.*;

@WebFilter(filterName = "/AuthorisationFilter", urlPatterns = {"/*"})
public class AuthorisationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorisationFilter.class);

    private static final Map<String, List<User.Role>> actionsMap = new HashMap<>();
    static {
        actionsMap.put("static",                            asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("dist",                              asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("uploads",                           asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("favicon.ico",                       asList(ADMINISTRATOR, MANAGER, USER));

        actionsMap.put("errorAction",                       asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("indexAction",                       asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("signInAction",                      asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("signUpAction",                      asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("logoutAction",                      asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("pageAction",                        asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("localeAction",                      asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("index.jsp",                         asList(ADMINISTRATOR, MANAGER, USER));

        actionsMap.put("requestAction",                     asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("bookAction",                        asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("myBookingsAction",                  asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("invoiceAction",                     asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("paymentAction",                     asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("cancelReservationAction",           asList(ADMINISTRATOR, MANAGER, USER));

        actionsMap.put("reservationRequestsAction",         asList(ADMINISTRATOR, MANAGER));
        actionsMap.put("proceedRequestAction",              asList(ADMINISTRATOR, MANAGER));
        actionsMap.put("makeReservationFromRequestAction",  asList(ADMINISTRATOR, MANAGER));
        actionsMap.put("hotelOccupancyAction",              asList(ADMINISTRATOR, MANAGER));
        actionsMap.put("allBookingsAction",                 asList(ADMINISTRATOR, MANAGER));
        actionsMap.put("setMaintenanceAction",              asList(ADMINISTRATOR, MANAGER));

        actionsMap.put("editUserAction",                    asList(ADMINISTRATOR, MANAGER, USER));
        actionsMap.put("manageUsersAction",                 asList(ADMINISTRATOR));
        actionsMap.put("manageCategoriesAction",            asList(ADMINISTRATOR));
        actionsMap.put("editCategoryAction",                asList(ADMINISTRATOR));
        actionsMap.put("manageRoomsAction",                 asList(ADMINISTRATOR));
        actionsMap.put("editRoomAction",                    asList(ADMINISTRATOR));
        actionsMap.put("FileUploadServlet",                 asList(ADMINISTRATOR));
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String uri = req.getServletPath().split("/")[1];

        User user = null;

        if (session != null && session.getAttribute("user") != null)
            user = (User) session.getAttribute("user");

        if (user != null) {
            List<User.Role> allowedRoles = actionsMap.get(uri);

            if (allowedRoles == null) {
                LOGGER.warn("{} - allowed roles not assigned!", uri);
            }

            if (allowedRoles == null || !allowedRoles.contains(user.getRole())) {
                LOGGER.warn("Disallowed request attempt! {} - {}", uri, req.getRemoteAddr());
                res.sendRedirect("indexAction");
                return;
            }
        }

        chain.doFilter(request, response);

    }
}
