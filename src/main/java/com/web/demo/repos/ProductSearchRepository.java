package com.web.demo.repos;

import com.product.dtos.ProductElasticSearchRequest;
import com.web.demo.documents.ProductDocument;
import org.springframework.data.domain.Page;

public interface ProductSearchRepository {

    Page<ProductDocument> search(ProductElasticSearchRequest request);

    void save(ProductDocument document);

    void saveAll(Iterable<ProductDocument> documents);
}
