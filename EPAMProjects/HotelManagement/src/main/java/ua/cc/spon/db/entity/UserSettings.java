package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Sergiy Ponomarenko
 */
@Data
public class UserSettings implements Entity, Serializable {

    private int id;
    private int userId;
    private String locale;
    private String hash;

    public String generateHash() {
        hash = UUID.randomUUID().toString().replace("-", "").substring(0, 30);
        return hash;
    }
}
