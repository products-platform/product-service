package com.web.demo.services;

import com.product.dtos.ProductElasticSearchRequest;
import com.product.dtos.ProductElasticSearchResponse;
import org.springframework.data.domain.Page;

public interface ProductSearchService {
    Page<ProductElasticSearchResponse> search(ProductElasticSearchRequest request);
}
