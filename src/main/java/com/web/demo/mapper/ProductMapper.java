package com.web.demo.mapper;

import com.product.dtos.ProductRequest;
import com.web.demo.documents.ProductDocument;
import com.web.demo.models.Product;

import java.util.List;

public interface ProductMapper {
    ProductDocument toDocument(Product product);

    Product buildEntity(ProductRequest request);

    List<ProductRequest> toRecords(List<Product> products);

    ProductRequest toRecord(Product product);

    void patchProduct(Product product, ProductRequest updatedData);

    ProductRequest mapToResponseFromDocument(ProductDocument content);
}
