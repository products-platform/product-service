package com.web.demo.services;

import com.product.dtos.ProductRequest;
import com.product.dtos.ProductResponse;
import com.product.dtos.ProductSearchRequest;
import com.web.demo.records.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface ProductService {
    List<ProductDto> getAllProducts();

    ProductDto getProductByIdJsonFile(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAll(int page, int size);

    void delete(Long id);

    List<ProductResponse> getProductsByIds(
            Set<Long> ids);

    List<ProductResponse> searchProducts(ProductSearchRequest productSearchRequest);
}
