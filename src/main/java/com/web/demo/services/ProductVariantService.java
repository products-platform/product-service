package com.web.demo.services;

import com.product.dtos.ProductVariantRequest;
import com.web.demo.models.ProductVariant;

import java.util.List;

public interface ProductVariantService {
    ProductVariant addVariant(Long productId, ProductVariantRequest request);

    List<ProductVariant> getVariants(Long productId);
}
