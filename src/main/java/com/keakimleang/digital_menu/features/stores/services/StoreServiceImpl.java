package com.keakimleang.digital_menu.features.stores.services;

import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.updates.*;
import com.keakimleang.digital_menu.features.stores.payloads.response.*;
import com.keakimleang.digital_menu.features.stores.repos.*;
import com.keakimleang.digital_menu.features.sysparams.entities.*;
import com.keakimleang.digital_menu.features.sysparams.repos.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import com.keakimleang.digital_menu.features.users.services.*;
import com.keakimleang.digital_menu.utils.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl {
    private final StoreRepository storeRepository;
    private final OperatingHourRepository operatingHourRepository;
    private final OrderingOptionRepository orderingOptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FeeRangeRepository feeRangeRepository;
    private final GroupRepository groupRepository;
    private final GroupServiceImpl groupServiceImpl;
    private final SysParamRepository sysParamRepository;

    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORE, key = "#user.id"),
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
    })
    public StoreResponse createStore(User user, CreateStoreRequest request) {
        try {
            Store store = CreateStoreRequest.fromRequest(request);
            store.setSlug(Slugify.slugify(request.getName()));
            store.setCreatedBy(user.getId());
            Store savedStore = storeRepository.save(store);
            SysParam sysParam = new SysParam();
            sysParam.setMaxCategoryNumber(SysParamValue.MAX_CATEGORY);
            sysParam.setMaxMenuNumber(SysParamValue.MAX_MENU);
            sysParam.setStoreId(savedStore.getId());
            sysParamRepository.save(sysParam);
            processOrderingOptions(savedStore, request);
            processOperatingHours(savedStore);
            processPaymentMethods(savedStore);
            return StoreResponse.fromEntity(savedStore);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Store name already exists", e);
        } catch (Exception e) {
            log.error("Error while creating store", e);
            throw new RuntimeException("Error while creating store", e);
        }
    }

    private void processOrderingOptions(Store store, CreateStoreRequest request) {
        try {
            List<OrderingOption> orderingOptions = CreateOrderingOptionRequest
                    .fromRequests(request.getOrderOptions(), store);
            orderingOptionRepository.saveAll(orderingOptions);

            orderingOptions.forEach(option ->
                    feeRangeRepository.saveAll(CreateFeeRangeRequest.fromRequests(option, option.getFeeRanges())));
        } catch (Exception e) {
            log.error("Error while processing ordering options", e);
            log.error("Skipping processing ordering options");
        }
    }

    private void processOperatingHours(Store store) {
        try {
            List<OperatingHour> operatingHours = operatingHourRepository
                    .findAllById(store.getOperatingHours().stream().map(OperatingHour::getId).toList())
                    .stream().peek(hour -> hour.setStore(store)).toList();
            operatingHourRepository.saveAll(operatingHours);
        } catch (Exception e) {
            log.error("Error while processing operating hours", e);
            log.error("Skipping processing operating hours");
        }
    }

    private void processPaymentMethods(Store store) {
        try {
            List<PaymentMethod> paymentMethods = paymentMethodRepository
                    .findAllById(store.getPaymentMethods().stream().map(PaymentMethod::getId).toList())
                    .stream().peek(method -> method.setStore(store)).toList();
            paymentMethodRepository.saveAll(paymentMethods);
        } catch (Exception e) {
            log.error("Error while processing payment methods", e);
            log.error("Skipping processing payment methods");
        }
    }

    @Transactional
    @Cacheable(value = CacheValue.STORE, key = "#user.id")
    public List<StoreResponse> findAllStoresByUser(User user) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(AuthRole.admin));
        return storeRepository.findAll().stream()
                .map(store -> {
                    StoreResponse response = StoreResponse.fromEntity(store);
                    if (isAdmin || ResourceOwner.hasPermission(user, store)) {
                        response.setHasPrivilege(true);
                    }
                    return response;
                })
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORE, key = "#user.id"),
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
    })
    public StoreResponse deleteStoreById(User user, UUID id) {
        Store store = storeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Store", id.toString())
        );
        if (ResourceOwner.hasPermission(user, store)) {
            storeRepository.delete(store);
            return StoreResponse.fromEntity(store);
        } else {
            throw new ResourceForbiddenException(user.getUsername(), Store.class);
        }
    }

    @Transactional
    @Cacheable(value = CacheValue.STORE, key = "#user.id")
    public StoreResponse getStore(User user, UUID id) {
        Store store = storeRepository.findById(id).orElseThrow();
        StoreResponse response = StoreResponse.fromEntity(store);
        user.getRoles().forEach(role -> {
            if (role.getName().equals(AuthRole.admin) || user.getId().equals(store.getCreatedBy())) {
                response.setHasPrivilege(true);
            }
        });
        return response;
    }

    @Transactional
    @Cacheable(value = CacheValue.STORE, key = "#user.id")
    public StoreResponse findMyStore(User user) {
        Group group = groupServiceImpl.findGroupByUser(user);
        if (group == null) {
            throw new ResourceNotFoundException("Group", "User ID: " + user.getId());
        }
        Store store = storeRepository.findByGroupId(group.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Store", "Group ID: " + group.getId()));
        return StoreResponse.fromEntity(store);
    }

    @Transactional
    public List<StoreResponse> assignStoreToUserGroup(User user, AssignGroupRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", request.getGroupId().toString()));
        return request.getStoreIds().stream()
                .map(id -> {
                    Store store = storeRepository.findById(id).orElseThrow();
                    store.setGroup(group);
                    store.setUpdatedAt(LocalDateTime.now());
                    store.setUpdatedBy(user.getId());
                    storeRepository.save(store);
                    return StoreResponse.fromEntity(store);
                }).toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORE, key = "#user.id"),
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
            @CacheEvict(value = CacheValue.STORE, key = "#request.slug"),
    })
    public StoreResponse updateStoreById(User user, UUID id, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", id.toString()));
        log.info(request.toString());
        if (ResourceOwner.hasPermission(user, store)) {
            updateStoreDetails(user, store, request);
            operatingHourRepository.deleteAllByStoreId(id);
            List<OperatingHour> operatingHours = request.getOperatingHours()
                    .stream().map(hour -> {
                        OperatingHour operatingHour = new OperatingHour();
                        operatingHour.setStore(store);
                        operatingHour.setDay(hour.getDay());
                        operatingHour.setOpenTime(hour.getOpenTime());
                        operatingHour.setCloseTime(hour.getCloseTime());
                        return operatingHour;
                    }).toList();
            operatingHourRepository.saveAll(operatingHours);

            feeRangeRepository.deleteAllByOrderingOptionStoreId(id);
            orderingOptionRepository.deleteAllByStoreId(id);
            List<OrderingOption> orderingOptions = request.getOrderOptions()
                    .stream().map(option -> {
                        OrderingOption orderingOption = new OrderingOption();
                        orderingOption.setStore(store);
                        orderingOption.setName(option.getName());
                        orderingOption.setDescription(option.getDescription());
                        return orderingOption;
                    }).toList();
            orderingOptionRepository.saveAll(orderingOptions);

            // Handle fee ranges for ordering options
            Map<String, OrderingOption> savedOptionsMap = orderingOptions.stream()
                    .collect(Collectors.toMap(OrderingOption::getName, option -> option));
            List<FeeRange> allFeeRanges = new ArrayList<>();
            for (int i = 0; i < request.getOrderOptions().size(); i++) {
                UpdateOrderingOptionRequest optionReq = request.getOrderOptions().get(i);
                OrderingOption savedOption = savedOptionsMap.get(optionReq.getName());
                List<FeeRange> feeRanges = optionReq.getFeeRanges()
                        .stream().map(rangeReq -> {
                            FeeRange feeRange = new FeeRange();
                            feeRange.setOrderingOption(savedOption);
                            feeRange.setCondition(rangeReq.getCondition());
                            feeRange.setFee(rangeReq.getFee());
                            return feeRange;
                        }).toList();
                allFeeRanges.addAll(feeRanges);
            }
            feeRangeRepository.saveAll(allFeeRanges);

            paymentMethodRepository.deleteAllByStoreId(id);
            List<PaymentMethod> paymentMethods = request.getPaymentMethods()
                    .stream().map(method -> {
                        PaymentMethod paymentMethod = new PaymentMethod();
                        paymentMethod.setStore(store);
                        paymentMethod.setMethod(method.getMethod());
                        return paymentMethod;
                    }).toList();
            paymentMethodRepository.saveAll(paymentMethods);
            return StoreResponse.fromEntity(store);
        } else {
            throw new ResourceForbiddenException(user.getUsername(), Store.class);
        }
    }

    private void updateStoreDetails(User user, Store store, UpdateStoreRequest request) {
        store.setName(request.getName());
        store.setLogo(request.getLogo());
        store.setColor(request.getColor());
        store.setDescription(request.getDescription());
        store.setPhysicalAddress(request.getPhysicalAddress());
        store.setVirtualAddress(request.getVirtualAddress());
        store.setPhone(request.getPhone());
        store.setEmail(request.getEmail());
        store.setWebsite(request.getWebsite());
        store.setFacebook(request.getFacebook());
        store.setInstagram(request.getInstagram());
        store.setTelegram(request.getTelegram());
        store.setColor(request.getColor());
        store.setBanner(request.getBanner());
        store.setUpdatedAt(LocalDateTime.now());
        store.setUpdatedBy(user.getId());
        store.setLat(request.getLat());
        store.setLng(request.getLng());
        store.setShowGoogleMap(request.getShowGoogleMap());
        storeRepository.save(store);
    }

    @Transactional
    @Cacheable(value = CacheValue.STORE, key = "#slug")
    public StoreResponse findByStoreSlug(String slug) {
        Store store = storeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Store", slug));
        return StoreResponse.fromEntity(store);
    }

    @Transactional
    @Cacheable(value = CacheValue.STORE_ENTITY, key = "#user.id")
    public Store findStoreEntityById(User user, UUID storeId) {
        if (user == null) throw new ResourceForbiddenException("Unknown user", Store.class);
        return storeRepository.findById(Optional.ofNullable(storeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Store", "unknown")))
                .orElseThrow(() -> new ResourceNotFoundException("Store", storeId.toString()));
    }

    @Transactional
    public Store findStoreEntityById(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", storeId.toString()));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORE, key = "#user.id"),
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
            @CacheEvict(value = CacheValue.STORE, key = "#store.slug"),
    })
    public StoreResponse updateStoreLayoutById(User user, Store store, String layout) {
        if (ResourceOwner.hasPermission(user, store)) {
            store.setLayout(layout);
            store.setUpdatedAt(LocalDateTime.now());
            store.setUpdatedBy(user.getId());
            Store saved = storeRepository.save(store);
            return StoreResponse.fromEntity(saved);
        } else {
            throw new ResourceForbiddenException(user.getUsername(), Store.class);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORE, key = "#userId"),
            @CacheEvict(value = CacheValue.STORES, key = "#userId"),
            @CacheEvict(value = CacheValue.STORE, key = "#store.slug"),
    })
    public void updateStoreImage(UUID userId, Store store, String imageType, String name) {
        switch (imageType) {
            case "promotion" -> store.setPromotion(name);
            case "banner" -> store.setBanner(name);
            case "logo" -> store.setLogo(name);
            default -> throw new IllegalArgumentException("Invalid image type");
        }
        store.setUpdatedAt(LocalDateTime.now());
        store.setUpdatedBy(userId);
        storeRepository.save(store);
    }
}
