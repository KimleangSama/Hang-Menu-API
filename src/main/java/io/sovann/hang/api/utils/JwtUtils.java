package io.sovann.hang.api.utils;


import lombok.Getter;

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

