package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserSettings implements Serializable {

    private long id;
    private long userId;
    private String locale;
    private String hash;

    public String generateHash() {

        hash = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
        return hash;

    }
}