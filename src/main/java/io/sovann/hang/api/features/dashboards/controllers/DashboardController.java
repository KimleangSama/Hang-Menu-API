package io.sovann.hang.api.features.dashboards.controllers;

import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.dashboards.payloads.OverviewResponse;
import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.repos.CategoryRepository;
import io.sovann.hang.api.features.menus.repos.MenuRepository;
import io.sovann.hang.api.features.orders.repos.OrderRepository;
import io.sovann.hang.api.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
