package ua.cc.spon.util;

public class Constants {

    private Constants() {}

    public static final String SIGN_IN_URL = "sign-in.jsp";
    public static final String SIGN_UP_URL = "sign-up.jsp";
    public static final String CATEGORIES_URL = "categories.jsp";
    public static final String MY_BOOKINGS_URL = "my-bookings.jsp";
    public static final String ALL_BOOKINGS_URL = "all-bookings.jsp";
    public static final String USERS_URL = "users.jsp";
    public static final String ROOMS_URL = "rooms.jsp";
    public static final String EDIT_CATEGORY_URL = "edit-category.jsp";
    public static final String EDIT_ROOM_URL = "edit-room.jsp";

    public static final String EMAIL_PATTERN = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    public static final String FIRST_LAST_NAME_PATTERN = ".*";
    public static final String LOCALE_PATTERN = "^[A-Za-z]{2,5}$";
    public static final String ANY_SYMBOLS = "(?is).*";
}
