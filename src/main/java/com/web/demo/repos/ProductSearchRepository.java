package com.web.demo.repos;

import com.product.dtos.ProductElasticeSearchRequest;
import com.web.demo.documents.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, Long> {

    Page<ProductDocument> search(ProductElasticeSearchRequest request);
}
