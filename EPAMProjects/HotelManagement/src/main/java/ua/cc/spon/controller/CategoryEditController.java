package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.EmptyDescriptionException;
import ua.cc.spon.exception.EmptyNameException;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ua.cc.spon.util.Constants.EDIT_CATEGORY_URL;

@WebServlet({"/editCategoryAction"})
public class CategoryEditController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryEditController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int categoryId;

        try {
            categoryId = validator.validateAndGetInt("category_id", new IllegalArgumentException());

        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("manageCategoriesAction");
            return;
        }

        transaction.init(roomCategoryDAO);

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {
            try {
                roomCategoryDAO.delete(categoryId);
            } catch (DaoException e) {
                LOGGER.error(e.getMessage(), e);
                req.getSession().setAttribute("fail_message", "error.someDBError");

            } finally {
                transaction.end();
            }
            resp.sendRedirect("manageCategoriesAction");

        } else {

            Map<String, RoomCategory> editCategoryMap;

            transaction.initTransaction(roomCategoryDAO);

            try {

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
                        LOGGER.warn(e.getMessage());
                        transaction.rollback();
                        req.getSession().setAttribute("fail_message", "error." + e.getMessage());
                        resp.sendRedirect("editCategoryAction?category_id=" + categoryId);
                        return;
                    }

                    RoomCategory roomCategory = editCategoryMap.get(l);
                    roomCategory.setName(categoryName);
                    roomCategory.setDescription(categoryDescription);

                    roomCategoryDAO.update(roomCategory, l);
                }

                transaction.commit();

                LOGGER.info("RoomCategory #{} saved", categoryId);

                resp.sendRedirect("manageCategoriesAction");

            } catch (DaoException e) {
                LOGGER.error(e.getMessage(), e);
                transaction.rollback();
                req.getSession().setAttribute("fail_message", "error.someDBError");
                resp.sendRedirect("editCategoryAction?category_id=" + categoryId);
            } finally {
                transaction.endTransaction();
            }


        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();
        LocaleDAO localeDAO = factory.getLocaleDAO();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int categoryId = validator.validateAndGetInt("category_id", -1);

        Map<String, RoomCategory> editCategoryMap = new HashMap<>();

        transaction.initTransaction(localeDAO, roomCategoryDAO);

        try {
            for (Locale loc : localeDAO.findAllMapByName().values()) {
                editCategoryMap.put(loc.getName(), new RoomCategory());
            }

            if (req.getParameter("new") == null) {
                editCategoryMap = roomCategoryDAO.findByIdGroupByLocale(categoryId);
            }

            transaction.commit();

            req.setAttribute("editCategoryMap", editCategoryMap);
            req.setAttribute("categoryId", categoryId);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(EDIT_CATEGORY_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("manageCategoriesAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
