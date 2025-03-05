package io.sovann.hang.api.utils;

import java.time.*;

public class DateUtils {
    public static LocalDateTime getStartDateOfLastWeek() {
        LocalDate today = LocalDate.now();
        LocalDate lastWeekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        return lastWeekStart.atStartOfDay();
    }

    public static LocalDateTime getEndDateOfLastWeek() {
        LocalDate today = LocalDate.now();
        LocalDate lastWeekEnd = today.minusWeeks(1).with(DayOfWeek.SUNDAY);
        return lastWeekEnd.atTime(23, 59, 59);
    }
}
