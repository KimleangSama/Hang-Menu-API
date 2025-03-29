package com.keakimleang.digital_menu.utils;


import lombok.*;

@Getter
public enum JwtUtils {
    USER_ID("idUser"),
    FULLNAME("fullname"),
    EXPIRE("ieExpire"),
    SCOPE("scope");

    private final String property;

    JwtUtils(String property) {
        this.property = property;
    }
}

