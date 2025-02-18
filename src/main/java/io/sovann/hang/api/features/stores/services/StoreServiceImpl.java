package io.sovann.hang.api.features.stores.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.payloads.request.AssignGroupRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateFeeRangeRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateOrderingOptionRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateStoreRequest;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
    private final LanguageRepository languageRepository;

    public long count() {
        return storeRepository.count();
    }

    @CacheEvict(value = "store", key = "#user.getId()", allEntries = true)
    public StoreResponse createStore(User user, CreateStoreRequest request) {
        try {
            Store savedStore = createAndSaveStore(user, request);
            processOrderingOptions(savedStore, request);
            processOperatingHours(savedStore);
            processPaymentMethods(savedStore);
            processLanguages(savedStore);
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

    private void processLanguages(Store store) {
        try {
            List<Language> languages = languageRepository
                    .findAllById(store.getLanguages().stream().map(Language::getId).toList())
                    .stream().peek(option -> option.setStore(store)).toList();
            languageRepository.saveAll(languages);
        } catch (Exception e) {
            log.error("Error while processing language options", e);
            log.error("Skipping processing language options");
        }
    }

    @Transactional
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

    public StoreResponse deleteStore(UUID id) {
        Store store = storeRepository.findById(id).orElseThrow();
        storeRepository.delete(store);
        return StoreResponse.fromEntity(store);
    }

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
    @Cacheable(value = "store", key = "#user.getId()")
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
    @CacheEvict(value = "store", key = "#user.getId()", allEntries = true)
    public StoreResponse updateStore(User user, UUID id, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", id.toString()));
        updateStoreDetails(store, request, user);
        updateEntities(
                request.getOperatingHours(),
                operatingHourRepository,
                OperatingHour::getId,
                (hour, req) -> {
                    hour.setDay(req.getDay());
                    hour.setOpenTime(req.getOpenTime());
                    hour.setCloseTime(req.getCloseTime());
                    hour.setStore(store);
                },
                OperatingHour::new
        );

        updateEntities(
                request.getOrderOptions(),
                orderingOptionRepository,
                OrderingOption::getId,
                (option, req) -> {
                    option.setName(req.getName());
                    option.setDescription(req.getDescription());
                    option.setStore(store);
                    updateEntities(req.getFeeRanges(), feeRangeRepository, FeeRange::getId,
                            (range, rangeReq) -> {
                                orderingOptionRepository.save(option);
                                range.setCondition(rangeReq.getCondition());
                                range.setFee(rangeReq.getFee());
                                range.setOrderingOption(option);
                            },
                            FeeRange::new);
                },
                OrderingOption::new
        );

        updateEntities(
                request.getPaymentMethods(),
                paymentMethodRepository,
                PaymentMethod::getId,
                (method, req) -> {
                    method.setStore(store);
                    method.setMethod(req.getMethod());
                },
                PaymentMethod::new
        );

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
        storeRepository.save(store);
    }

    private <T, R> void updateEntities(
            List<R> requestList,
            JpaRepository<T, UUID> repository,
            Function<T, UUID> idGetter,
            BiConsumer<T, R> updater,
            Supplier<T> entitySupplier) {
        if (!(repository instanceof FeeRangeRepository)) {
            repository.deleteAll();
        }
        if (requestList == null || requestList.isEmpty()) return;
        Map<UUID, R> requestMap = requestList.stream()
                .collect(Collectors.toMap(this::extractId, Function.identity()));
        List<T> existingEntities = repository.findAllById(requestMap.keySet());
        Map<UUID, T> existingEntitiesMap = existingEntities.stream()
                .collect(Collectors.toMap(idGetter, Function.identity()));
        List<T> allEntities = new ArrayList<>();
        for (R request : requestList) {
            UUID id = extractId(request);
            T entity = existingEntitiesMap.getOrDefault(id, entitySupplier.get()); // Use existing or create new

            updater.accept(entity, request);
            allEntities.add(entity);
        }
        repository.saveAll(allEntities);
    }

    private <R> UUID extractId(R request) {
        try {
            return (UUID) request.getClass().getMethod("getId").invoke(request);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to extract ID from request object", e);
        }
    }

    @Transactional
    @Cacheable(value = "store", key = "#slug")
    public StoreResponse getStoreByNameSlug(String slug) {
        Store store = storeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Store", slug));
        return StoreResponse.fromEntity(store);
    }
}
