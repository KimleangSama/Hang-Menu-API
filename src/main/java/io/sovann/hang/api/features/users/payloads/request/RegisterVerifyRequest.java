package io.sovann.hang.api.features.users.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RegisterVerifyRequest {
    private String email;
    private String username;
    private String code;
}