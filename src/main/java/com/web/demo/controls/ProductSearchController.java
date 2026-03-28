package com.web.demo.controls;

import com.product.dtos.*;
import com.web.demo.services.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @PostMapping
    public Page<ProductElasticSearchResponse> search(
            @RequestBody ProductElasticSearchRequest request) {
        return productSearchService.search(request);
    }

    @GetMapping("/products")
    public List<ProductRequest> searchProducts(
            @RequestParam String keyword) {

        return productSearchService.searchProducts(keyword);
    }

    @PostMapping("/multiple")
    public List<ProductRequest> searchMultiple(
            @RequestBody MultipleProductSearchRequest multipleProductSearchRequest) {
        return productSearchService.searchMultiple(multipleProductSearchRequest);
    }

}
