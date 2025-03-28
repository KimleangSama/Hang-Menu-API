package com.keakimleang.digital_menu.features.users.payloads.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RegisterVerifyRequest {
    private String email;
    private String username;
    private String code;
}