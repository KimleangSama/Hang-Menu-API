package io.sovann.hang.api.features.feedbacks.enums;

import lombok.Getter;

@Getter
public enum Rating {
    NONE(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    Rating(int value) {
        this.value = value;
    }
}
