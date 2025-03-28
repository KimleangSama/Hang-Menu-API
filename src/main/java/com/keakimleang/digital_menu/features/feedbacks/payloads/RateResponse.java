package com.keakimleang.digital_menu.features.feedbacks.payloads;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RateResponse {
    private double rate;
    private double amount;
}
