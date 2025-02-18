package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.features.orders.repos.OrderMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMenuServiceImpl {
    private final OrderMenuRepository orderMenuRepository;
}
