package com.logicarchive.dto;

import com.logicarchive.entity.User;

public class UserResponse {

    private Long id;
    private String name;
    private String email;

    public UserResponse() {
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
