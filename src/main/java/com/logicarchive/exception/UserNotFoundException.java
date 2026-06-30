package com.logicarchive.exception;


import com.logicarchive.entity.User;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg){
        super(msg);
    }
}
