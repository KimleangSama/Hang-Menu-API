package com.keakimleang.digital_menu.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WithRateLimitProtection {
}