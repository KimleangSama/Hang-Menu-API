package io.sovann.hang.api.features.users.payloads.request;

import io.sovann.hang.api.features.users.enums.AuthRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RegisterBackOfficeRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private List<AuthRole> roles;
}