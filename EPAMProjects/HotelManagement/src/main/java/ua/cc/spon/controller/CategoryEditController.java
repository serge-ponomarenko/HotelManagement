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
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.EmptyDescriptionException;
import ua.cc.spon.exception.EmptyNameException;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet({"/editCategoryAction"})
public class CategoryEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long categoryId;

        try {
            categoryId = validator.validateAndGetLong("category_id", new IllegalArgumentException());

        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("manageCategoriesAction");
            return;
        }

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {
            try {
                roomCategoryDAO.deleteById(categoryId);
            } catch (DBException e) {
                req.getSession().setAttribute("fail_message", "error.someDBError");
            }
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

            String categoryName;
            String categoryDescription;
            try {
                categoryName = validator.validateAndGetString("name_" + l, Constants.ANY_SYMBOLS, new EmptyNameException());
                categoryDescription = validator.validateAndGetString("description_" + l, Constants.ANY_SYMBOLS, new EmptyDescriptionException());
            } catch (EmptyNameException | EmptyDescriptionException e) {
                req.setAttribute("fail_message", "error." + e.getMessage());
                resp.sendRedirect("editCategoryAction?category_id=" + categoryId);
                return;
            }

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

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long categoryId = validator.validateAndGetLong("category_id", -1L);

        Map<String, RoomCategory> editCategoryMap = new HashMap<>();

        for (Locale loc : localeDAO.findALL().values()) {
            editCategoryMap.put(loc.getName(), new RoomCategory());
        }

        if (req.getParameter("new") == null) {
            editCategoryMap = roomCategoryDAO.findByIdGroupByLocale(categoryId);
        }

        req.setAttribute("editCategoryMap", editCategoryMap);
        req.setAttribute("categoryId", categoryId);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher(Constants.EDIT_CATEGORY_URL).forward(req, resp);

    }

}
