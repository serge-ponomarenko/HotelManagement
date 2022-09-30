package ua.cc.spon.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class RequestParametersValidatorService {

    private final HttpServletRequest req;

    public RequestParametersValidatorService(HttpServletRequest req) {
        this.req = req;
    }

    public <T extends Exception> String validateAndGetString(String parameter, String pattern, T exception) throws T {
        String result = req.getParameter(parameter);

        if (result == null || result.isBlank()) throw exception;
        if (!result.matches(pattern)) throw exception;

        return result;
    }

    public String validateAndGetString(String parameter, String def)  {
        String result = req.getParameter(parameter);

        if (result == null || result.isBlank()) return def;

        return result;
    }

    public boolean validateAndGetBoolean(String parameter) {
        String result = req.getParameter(parameter);

        return result != null && !result.isBlank() && !result.equalsIgnoreCase("false");
    }

    public <T extends Exception> LocalDate validateAndGetDate(String parameter, T exception) throws T {
        String param = req.getParameter(parameter);
        if (param == null || param.isBlank()) throw exception;

        LocalDate result;

        try {
            result = LocalDate.parse(param);
        } catch (DateTimeParseException e) {
            throw exception;
        }

        return result;
    }

    public LocalDate validateAndGetDate(String parameter, LocalDate def) {
        String param = req.getParameter(parameter);
        if (param == null || param.isBlank()) return def;

        LocalDate result = def;

        try {
            result = LocalDate.parse(param);
        } catch (DateTimeParseException ignored) {}

        return result;
    }

    public int validateAndGetInt(String parameter, int def) {
        String param = req.getParameter(parameter);
        if (param == null || param.isBlank()) return def;

        int result = def;

        try {
            result = Integer.parseInt(param);
        } catch (NumberFormatException ignored) {}

        return result;
    }

    public <T extends Exception> int validateAndGetInt(String parameter, T exception) throws T {
        String param = req.getParameter(parameter);
        if (param == null || param.isBlank()) throw exception;

        int result;
        try {
            result = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw exception;
        }

        return result;
    }

    public <T extends Exception> List<Integer> validateAndGetIntArray(String parameter, T exception) throws T {
        String[] param = req.getParameterValues(parameter);
        List<Integer> result = new ArrayList<>();

        if (param == null || param.length == 0) throw exception;

        try {
            for (String s : param) {
                result.add(Integer.parseInt(s));
            }
        } catch (NumberFormatException e) {
            throw exception;
        }

        return result;
    }

    public List<Integer> validateAndGetIntArray(String parameter) {
        String[] param = req.getParameterValues(parameter);
        List<Integer> result = new ArrayList<>();

        if (param == null || param.length == 0) return result;

        try {
            for (String s : param) {
                result.add(Integer.parseInt(s));
            }
        } catch (NumberFormatException e) {
            result = new ArrayList<>();
        }

        return result;
    }

    public BigDecimal validateAndGetBigDecimal(String parameter, BigDecimal def) {
        String param = req.getParameter(parameter);
        if (param == null || param.isBlank()) return def;

        BigDecimal result = def;

        try {
            result = new BigDecimal(param);
        } catch (NumberFormatException ignored) {}

        return result;
    }

    public <T extends Exception> BigDecimal validateAndGetBigDecimal(String parameter, T exception) throws T {
        String param = req.getParameter(parameter);
        if (param == null || param.isBlank()) throw exception;

        BigDecimal result;

        try {
            result = new BigDecimal(param);
        } catch (NumberFormatException e) {
            throw exception;
        }

        return result;
    }


}
