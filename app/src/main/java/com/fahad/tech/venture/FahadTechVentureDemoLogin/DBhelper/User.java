package com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public boolean isAdmin;
    public long signupTimestamp; // Field for signup time

    // Constructor
    public User(String username, String firstName, String lastName, String email, String password, boolean isAdmin, long signupTimestamp) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.signupTimestamp = signupTimestamp;
    }
}
