package com.web.demo.services;

import com.product.dtos.ProductElasticSearchRequest;
import com.product.dtos.ProductElasticSearchResponse;
import com.product.dtos.VariantResponse;
import com.web.demo.documents.ProductDocument;
import com.web.demo.repos.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    public Page<ProductElasticSearchResponse> search(ProductElasticSearchRequest request) {
        return productSearchRepository.search(request)
                .map(this::map);
    }

    private ProductElasticSearchResponse map(ProductDocument doc) {
        return new ProductElasticSearchResponse(
                doc.getProductId(),
                doc.getName(),
                doc.getBrand(),
                doc.getCategory(),
                doc.getPrice(),
                doc.getVariantDocumentList().stream()
                        .map(v -> new VariantResponse(
                                v.getSize(),
                                v.getColor(),
                                v.getConfiguration(),
                                v.getPrice(),
                                v.getStock()
                        ))
                        .toList()
        );
    }

}
