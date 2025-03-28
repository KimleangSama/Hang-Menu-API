package com.keakimleang.digital_menu.commons.payloads;


import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.*;

@Getter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageInfo {
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;
    private final boolean first;
    private final boolean last;

    public PageInfo(int page, int size, long totalElements) {
        this.size = size;
        this.page = page + 1;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page + 1 == totalPages;
    }
}
