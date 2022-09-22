package ua.cc.spon.util;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class HotelHelper {

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
            throw new RuntimeException(e); // TODO: 23.08.2022
        }
        result = properties.getProperty(name);

        return result;

    }

    public static void proceedMessages(HttpServletRequest req) {

        String successMessage = null;
        if (req.getSession().getAttribute("success_message") != null) {
            successMessage = (String) req.getSession().getAttribute("success_message");
            req.getSession().removeAttribute("success_message");
        }
        String failMessage = null;
        if (req.getSession().getAttribute("fail_message") != null) {
            failMessage = (String) req.getSession().getAttribute("fail_message");
            req.getSession().removeAttribute("fail_message");
        }

        req.setAttribute("success_message", successMessage);
        req.setAttribute("fail_message", failMessage);

    }

    public static String getLocalizedString(String key) {
        ResourceBundle mybundle = ResourceBundle.getBundle("Strings", Locale.getDefault(), new ResourceBundle.Control() {
            @Override
            public List<Locale> getCandidateLocales(String baseName, Locale locale) {
                List<Locale> list = super.getCandidateLocales(baseName, locale);
                list.add(new Locale("en", "US"));
                return list;
            }
        });
        return mybundle.getString(key);
    }


}
