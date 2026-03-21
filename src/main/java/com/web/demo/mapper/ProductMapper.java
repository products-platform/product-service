package com.web.demo.mapper;

import com.product.dtos.ProductRequest;
import com.web.demo.documents.ProductDocument;
import com.web.demo.models.Product;

public interface ProductMapper {
    ProductDocument toDocument(Product product);

    Product buildEntity(ProductRequest request);
}
