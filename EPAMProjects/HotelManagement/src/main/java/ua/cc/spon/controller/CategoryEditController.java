package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet({"/editCategoryAction"})
public class CategoryEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        long categoryId = Long.parseLong(Optional.ofNullable(req.getParameter("category_id")).orElse("-1"));

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {

            roomCategoryDAO.deleteById(categoryId);

            resp.sendRedirect("manageCategoriesAction");
            return;
        }


        Map<String, RoomCategory> editCategoryMap;

        if (categoryId == -1) {
            RoomCategory roomCategory = new RoomCategory();
            roomCategoryDAO.create(roomCategory);
            categoryId = roomCategory.getId();
        }

        editCategoryMap = roomCategoryDAO.findByIdGroupByLocale(categoryId);

        for (String l : editCategoryMap.keySet()) {
            String categoryName = req.getParameter("name_" + l);
            String categoryDescription = req.getParameter("description_" + l);

            RoomCategory roomCategory = editCategoryMap.get(l);
            roomCategory.setName(categoryName);
            roomCategory.setDescription(categoryDescription);

            roomCategoryDAO.update(roomCategory, l);

        }

        resp.sendRedirect("manageCategoriesAction");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();
        LocaleDAO localeDAO = factory.getLocaleDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        long categoryId = Long.parseLong(Optional.ofNullable(req.getParameter("category_id")).orElse("-1"));

        Map<String, RoomCategory> editCategoryMap = new HashMap<>();

        for (Locale loc: localeDAO.findALL().values()) {
            editCategoryMap.put(loc.getName(), new RoomCategory());
        }

        if (req.getParameter("new") == null) {
            editCategoryMap = roomCategoryDAO.findByIdGroupByLocale(categoryId);
        }

        req.setAttribute("editCategoryMap", editCategoryMap);
        req.setAttribute("categoryId", categoryId);

        req.getRequestDispatcher("edit-category.jsp").forward(req, resp);

    }

}
