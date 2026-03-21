package com.web.demo.records;

import java.util.List;

public record ProductDto(
        Long id,
        String name,
        int vendor_id,
        boolean requires_shipping,
        String sku,
        boolean taxable,
        String status,
        int price,
        Vendor vendor,
        List<TrackDetail> details
) {
}
