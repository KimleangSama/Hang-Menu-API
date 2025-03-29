package com.keakimleang.digital_menu.features.users.payloads.request;

import com.keakimleang.digital_menu.features.users.enums.*;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String fullname;
    @NotBlank
    private String password;
    private List<AuthRole> roles;
    private boolean isGroupMember = true;
}