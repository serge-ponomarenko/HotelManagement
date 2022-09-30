package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.exception.DaoException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/FileUploadServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,      // 10 MB
        maxFileSize = 1024 * 1024 * 50,                     // 50 MB
        maxRequestSize = 1024 * 1024 * 100)                 // 100 MB
public class FileUploadController extends HttpServlet {

    private static final long serialVersionUID = 205242440643911308L;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    /**
     * Directory where uploaded files will be saved, its relative to
     * the web application directory.
     */
    private static final String UPLOAD_DIR = "uploads";

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        // gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        ServletContext context = request.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomDAO roomDAO = factory.getRoomDAO();

        String fileName = null;
        String path = null;
        int roomId = -1;

        transaction.init(roomDAO);

        try {
            if (request.getParameter("action") != null && request.getParameter("action").equals("delete")) {
                path = request.getParameter("path");
                roomId = Integer.parseInt(request.getParameter("room_id"));

                roomDAO.deleteImage(roomId, path);

            } else {

                // creates the save directory if it does not exists
                File fileSaveDir = new File(uploadFilePath);
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdirs();
                }

                //Get all the parts from request and write it to the file on server
                for (Part part : request.getParts()) {
                    fileName = getFileName(part);
                    if (!fileName.isEmpty()) {
                        part.write(uploadFilePath + File.separator + fileName);
                        path = UPLOAD_DIR + File.separator + fileName;
                    } else {
                        roomId = Integer.parseInt(new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
                    }
                }

                roomDAO.addImage(roomId, path);

                LOGGER.info("Image added to Room #{}", roomId);
            }
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            request.getSession().setAttribute("fail_message", "error.someDBError");
        } finally {
            transaction.end();
        }

        response.sendRedirect("editRoomAction?room_id=" + roomId);
    }

    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

}
