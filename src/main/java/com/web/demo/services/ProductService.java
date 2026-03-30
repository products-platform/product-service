package com.web.demo.services;

import com.product.dtos.ProductRequest;
import com.product.dtos.ProductResponse;
import com.product.dtos.ProductSearchRequest;
import com.product.dtos.order.ProductRequestItem;
import com.product.dtos.product.ProductResponseDTO;
import com.web.demo.records.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface ProductService {
    List<ProductDto> getAllProducts();

    ProductDto getProductByIdJsonFile(Long id);

    ProductResponse createSingleProduct(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductRequest getProductById(Long id);

    Page<ProductResponse> getAll(int page, int size);

    void delete(Long id);

    List<ProductResponse> getProductsByIds(
            Set<Long> ids);

    List<ProductResponse> searchProducts(ProductSearchRequest productSearchRequest);

    String createBulkProducts(List<ProductRequest> requests);

    List<ProductRequest> findAllProducts();

    ProductRequest patchProduct(Long productId, ProductRequest updatedData);

    List<ProductResponseDTO> getProductsByProductAndVariant(List<ProductRequestItem> items);
}
