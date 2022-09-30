package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Sergiy Ponomarenko
 */
@Data
public class User implements Entity, Serializable {

    private int id;
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
