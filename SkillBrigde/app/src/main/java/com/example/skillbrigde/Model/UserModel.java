package com.example.skillbrigde.Model;
public class UserModel {
    public String name, mobile, email, username, password, role;

    public UserModel() {}

    public UserModel(String name, String mobile, String email,
                     String username, String password, String role) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
