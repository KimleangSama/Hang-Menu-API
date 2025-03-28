package com.keakimleang.digital_menu.features.dashboards.controllers;

import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.dashboards.payloads.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.repos.*;
import com.keakimleang.digital_menu.features.orders.repos.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.DASHBOARD)
@RequiredArgsConstructor
public class DashboardController {
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/overview")
    @Cacheable(value = "overview", key = "#storeId", sync = true, cacheNames = "overview")
    public BaseResponse<OverviewResponse> getOverview(@RequestParam UUID storeId) {
        List<Category> categories = categoryRepository.findAllByStoreId(storeId);
        Integer totalMenus = menuRepository.countByCategoryIn(categories);
        Integer totalOrders = orderRepository.countOrdersByStoreId(storeId);
        Integer totalRevenueUsd = orderRepository.sumTotalUsdByStoreId(storeId);
        Integer totalRevenueRiel = orderRepository.sumTotalRielByStoreId(storeId);
        Integer totalMenuLastWeek = menuRepository.countByCategoryInAndCreatedAtBetween(
                categories,
                DateUtils.getStartDateOfLastWeek(),
                DateUtils.getEndDateOfLastWeek()
        );
        Integer totalOrderLastWeek = orderRepository.countOrdersByStoreIdAndCreatedAtBetween(
                storeId,
                DateUtils.getStartDateOfLastWeek(),
                DateUtils.getEndDateOfLastWeek()
        );
        Integer totalRevenueUsdLastWeek = orderRepository.sumTotalUsdByStoreIdAndCreatedAtBetween(
                storeId,
                DateUtils.getStartDateOfLastWeek(),
                DateUtils.getEndDateOfLastWeek()
        );
        Integer totalRevenueRielLastWeek = orderRepository.sumTotalRielByStoreIdAndCreatedAtBetween(
                storeId,
                DateUtils.getStartDateOfLastWeek(),
                DateUtils.getEndDateOfLastWeek()
        );
        OverviewResponse response = new OverviewResponse();
        response.setTotalMenus(totalMenus);
        response.setTotalOrders(totalOrders);
        response.setTotalRevenueUsd(totalRevenueUsd);
        response.setTotalRevenueRiel(totalRevenueRiel);
        response.setTotalMenuLastWeek(totalMenuLastWeek);
        response.setTotalOrderLastWeek(totalOrderLastWeek);
        response.setTotalRevenueUsdLastWeek(totalRevenueUsdLastWeek);
        response.setTotalRevenueRielLastWeek(totalRevenueRielLastWeek);
        return BaseResponse.<OverviewResponse>ok()
                .setPayload(response);
    }
}
