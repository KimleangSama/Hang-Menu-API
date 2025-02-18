package io.sovann.hang.api.features.feedbacks.payloads;

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
