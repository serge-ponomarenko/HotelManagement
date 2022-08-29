package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private long id;
    private String email;
    private String hashPassword;
    private String firstName;
    private String lastName;

    private Role role;

    public enum Role {
        ADMINISTRATOR,
        MANAGER,
        USER
    }

}
