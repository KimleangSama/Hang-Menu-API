package io.sovann.hang.api.utils;

import org.springframework.util.StringUtils;

import java.util.UUID;

public class RandomString {
    public static String make(int length) {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "").substring(0, length);
    }
}
