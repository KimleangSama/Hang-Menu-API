package io.sovann.hang.api.features.stores.payloads.request.updates;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UpdateStoreRequest {
    private String name;
    private String logo;
    private String color;
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
    private Boolean showGoogleMap;

    private List<UpdateOperatingHourRequest> operatingHours;
    private List<UpdateOrderingOptionRequest> orderOptions;
    private List<UpdatePaymentMethodRequest> paymentMethods;
}
