package com.web.demo.services;

import com.product.dtos.ProductElasticeSearchRequest;
import com.web.demo.documents.ProductDocument;
import org.springframework.data.domain.Page;

public interface ProductSearchRepositoryCustom {

    Page<ProductDocument> search(ProductElasticeSearchRequest request);
}
