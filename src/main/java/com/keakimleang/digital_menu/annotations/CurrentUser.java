package com.keakimleang.digital_menu.annotations;

import java.lang.annotation.*;
import org.springframework.security.core.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {

}
