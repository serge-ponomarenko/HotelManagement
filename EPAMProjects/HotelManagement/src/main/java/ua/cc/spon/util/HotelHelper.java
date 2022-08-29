package ua.cc.spon.util;

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
