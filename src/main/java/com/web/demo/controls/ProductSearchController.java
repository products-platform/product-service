package com.web.demo.controls;

import com.product.dtos.ProductElasticSearchRequest;
import com.product.dtos.ProductElasticSearchResponse;
import com.product.dtos.ProductRequest;
import com.product.dtos.ProductResponse;
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
}
