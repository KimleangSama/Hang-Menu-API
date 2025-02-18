package io.sovann.hang.api.features.users.payloads.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginFrontOfficeRequest {
    private String email;
    private String password;
}
