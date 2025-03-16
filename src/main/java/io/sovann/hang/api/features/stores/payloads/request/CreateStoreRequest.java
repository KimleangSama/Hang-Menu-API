package io.sovann.hang.api.features.stores.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
@Setter
@ToString
public class CreateStoreRequest {
    private String name;
    private String logo;
    private String color = "#D22530";
    private String description;
    private String physicalAddress;
    private String virtualAddress;
    private String phone;
    private String email;
    private String website;
    private String facebook;
    private String telegram;
    private String instagram;
    private String cover;
    private String banner;
    private String layout;
    private Double lat;
    private Double lng;
    private Boolean showGoogleMap = true;

    private List<CreateOperatingHourRequest> operatingHours;
    private List<CreateOrderingOptionRequest> orderOptions;
    private List<CreatePaymentMethodRequest> paymentMethods;

    public static Store fromRequest(CreateStoreRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, Store.class);
    }
}
