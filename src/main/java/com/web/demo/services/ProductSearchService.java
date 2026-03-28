package com.web.demo.services;

import com.product.dtos.MultipleProductSearchRequest;
import com.product.dtos.ProductElasticSearchRequest;
import com.product.dtos.ProductElasticSearchResponse;
import com.product.dtos.ProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSearchService {
    Page<ProductElasticSearchResponse> search(ProductElasticSearchRequest request);

    List<ProductRequest> searchProducts(String keyword);

    List<ProductRequest> searchMultiple(MultipleProductSearchRequest productSearchRequest);
}
