package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Locale implements Serializable {

    private long id;
    private String name;
    private String iconPath;
    private String fullName;

}
