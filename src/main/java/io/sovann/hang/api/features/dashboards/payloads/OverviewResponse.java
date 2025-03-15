package io.sovann.hang.api.features.dashboards.payloads;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OverviewResponse {
    private Integer totalMenus;
    private Integer totalOrders;
    private Integer totalRevenueUsd;
    private Integer totalRevenueRiel;
    private Integer totalMenuLastWeek;
    private Integer totalOrderLastWeek;
    private Integer totalRevenueUsdLastWeek;
    private Integer totalRevenueRielLastWeek;
}
