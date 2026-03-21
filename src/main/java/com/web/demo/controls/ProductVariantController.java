package com.web.demo.controls;

import com.product.dtos.ProductVariantRequest;
import com.web.demo.models.ProductVariant;
import com.web.demo.services.ProductVariantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PostMapping("/{productId}/variants")
    public ProductVariant addVariant(
            @PathVariable Long productId,
            @RequestBody ProductVariantRequest request) {

        return productVariantService.addVariant(productId, request);
    }

    @GetMapping("/{productId}/variants")
    public List<ProductVariant> getVariants(@PathVariable Long productId) {
        return productVariantService.getVariants(productId);
    }
}
