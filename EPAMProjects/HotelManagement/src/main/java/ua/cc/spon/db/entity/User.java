package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class User implements Entity, Serializable {

    private long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Timestamp registeredDate;

    private Role role;

    public enum Role {
        ADMINISTRATOR,
        MANAGER,
        USER
    }

}
