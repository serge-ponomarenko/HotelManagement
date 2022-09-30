package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Class describes Locale entity.
 *
 * @author Sergiy Ponomarenko
 */
@Data
public class Locale implements Entity, Serializable {

    private int id;

    /**
     * Short Locale name.
     * Ex.: uk, en, de
     */
    private String name;

    /**
     * Country icon path.
     * Format: flag-country-ua
     */
    private String iconPath;

    /**
     * Full Locale name in native language.
     * Ex.: English, Українська, Deutsch.
     */
    private String fullName;

}
