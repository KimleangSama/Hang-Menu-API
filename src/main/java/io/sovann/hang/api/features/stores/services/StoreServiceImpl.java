package io.sovann.hang.api.features.stores.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.payloads.request.AssignGroupRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateFeeRangeRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateOrderingOptionRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateStoreRequest;
import io.sovann.hang.api.features.stores.payloads.request.updates.UpdateOrderingOptionRequest;
import io.sovann.hang.api.features.stores.payloads.request.updates.UpdateStoreRequest;
import io.sovann.hang.api.features.stores.payloads.response.StoreResponse;
import io.sovann.hang.api.features.stores.repos.*;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.features.users.repos.GroupRepository;
import io.sovann.hang.api.utils.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public long count() {
        return storeRepository.count();
    }

    @Caching(evict = {
            @CacheEvict(value = "store", key = "#user.id"),
            @CacheEvict(value = "stores", key = "#user.id")
    })
    public StoreResponse createStore(User user, CreateStoreRequest request) {
        try {
            Store savedStore = createAndSaveStore(user, request);
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

    private Store createAndSaveStore(User user, CreateStoreRequest request) {
        Store store = CreateStoreRequest.fromRequest(request);
        store.setSlug(Slugify.slugify(request.getName()));
        store.setCreatedBy(user.getId());
        return storeRepository.save(store);
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
    @Cacheable(value = "stores", key = "#user.id")
    public List<StoreResponse> listStores(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(AuthRole.admin));
        return storeRepository.findAll(pageable).stream()
                .map(store -> {
                    StoreResponse response = StoreResponse.fromEntity(store);
                    if (isAdmin || user.getId().equals(response.getCreatedBy())) {
                        response.setHasPrivilege(true);
                    }
                    return response;
                })
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "store", key = "#user.id"),
            @CacheEvict(value = "stores", key = "#user.id")
    })
    public StoreResponse deleteStore(User user, UUID id) {
        Store store = storeRepository.findById(id).orElseThrow();
        storeRepository.delete(store);
        return StoreResponse.fromEntity(store);
    }

    @Transactional
    @Cacheable(value = "store", key = "#user.id")
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
    @Cacheable(value = "store", key = "#user.id")
    public StoreResponse getMyStore(User user) {
        UUID groupId = user.getGroupMembers().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Group Member", user.getId().toString()))
                .getGroup().getId();
        List<Store> stores = storeRepository.findAllByGroupIdOrderByCreatedAt(groupId);
        if (stores.isEmpty()) {
            throw new ResourceNotFoundException("Store", "Group ID: " + groupId);
        }
        return StoreResponse.fromEntity(stores.getFirst());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "store", key = "#user.id"),
            @CacheEvict(value = "stores", key = "#user.id")
    })
    public List<StoreResponse> assignGroup(User user, AssignGroupRequest request) {
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
            @CacheEvict(value = "store", key = "#user.id"),
            @CacheEvict(value = "stores", key = "#user.id")
    })
    public StoreResponse updateStore(User user, UUID id, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", id.toString()));
        updateStoreDetails(store, request, user);

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
    }

    private void updateStoreDetails(Store store, UpdateStoreRequest request, User user) {
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
    @Cacheable(value = "store", key = "#slug")
    public StoreResponse getStoreByNameSlug(String slug) {
        Store store = storeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Store", slug));
        return StoreResponse.fromEntity(store);
    }

    @Transactional
    @Cacheable(value = "store-entity", key = "#storeId")
    public Store getStoreEntityById(User user, UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", storeId.toString()));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "store", key = "#user.id"),
            @CacheEvict(value = "store", key = "#slug"),
            @CacheEvict(value = "stores", key = "#user.id"),
    })
    public StoreResponse updateLayout(User user, String slug, String layout) {
        Store store = storeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Store", slug));
        store.setLayout(layout);
        store.setUpdatedAt(LocalDateTime.now());
        store.setUpdatedBy(user.getId());
        storeRepository.save(store);
        return StoreResponse.fromEntity(store);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "store", key = "#userId"),
            @CacheEvict(value = "store", key = "#store.id"),
            @CacheEvict(value = "store", key = "#store.slug"),
            @CacheEvict(value = "stores", key = "#userId"),
    })
    public void updateStorePromotionImage(UUID userId, Store store, String name) {
        store.setPromotion(name);
        store.setUpdatedAt(LocalDateTime.now());
        store.setUpdatedBy(userId);
        storeRepository.save(store);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "store", key = "#userId"),
            @CacheEvict(value = "store", key = "#store.id"),
            @CacheEvict(value = "store", key = "#store.slug"),
            @CacheEvict(value = "stores", key = "#userId"),
    })
    public void updateStoreBanner(UUID userId, Store store, String name) {
        store.setBanner(name);
        store.setUpdatedAt(LocalDateTime.now());
        store.setUpdatedBy(userId);
        storeRepository.save(store);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "store", key = "#userId"),
            @CacheEvict(value = "store", key = "#store.id"),
            @CacheEvict(value = "store", key = "#store.slug"),
            @CacheEvict(value = "stores", key = "#userId"),
    })
    public void updateStoreLogo(UUID userId, Store store, String name) {
        store.setLogo(name);
        store.setUpdatedAt(LocalDateTime.now());
        store.setUpdatedBy(userId);
        storeRepository.save(store);
    }
}
