package ua.cc.spon.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HotelHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelHelper.class);

    private HotelHelper() {
    }

    public static String getProperty(String name) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("app.properties");

        String result = "";
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        result = properties.getProperty(name);

        return result;

    }

    public static void proceedMessages(HttpServletRequest req) {
        String successMessageString = "success_message";
        String successMessage = null;

        if (req.getSession().getAttribute(successMessageString) != null) {
            successMessage = (String) req.getSession().getAttribute(successMessageString);
            req.getSession().removeAttribute(successMessageString);
        }

        String failMessageString = "fail_message";
        String failMessage = null;
        if (req.getSession().getAttribute(failMessageString) != null) {
            failMessage = (String) req.getSession().getAttribute(failMessageString);
            req.getSession().removeAttribute(failMessageString);
        }

        req.setAttribute(successMessageString, successMessage);
        req.setAttribute(failMessageString, failMessage);

    }

}
