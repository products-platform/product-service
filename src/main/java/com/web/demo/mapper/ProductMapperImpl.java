package com.web.demo.mapper;

import com.product.dtos.ProductRequest;
import com.web.demo.documents.ProductDocument;
import com.web.demo.documents.VariantDocument;
import com.web.demo.models.Product;
import com.web.demo.models.ProductVariant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapperImpl implements ProductMapper {

    public Product buildEntity(ProductRequest request) {
        Product product = new Product();
        product.setProductId(request.productId());
        product.setSku(request.sku());
        product.setName(request.name());
        product.setBrand(request.brand());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setWeightInGrams(request.weightInGrams());
        product.setDescription(request.description());

        List<ProductVariant> variants = request.variants().stream()
                .map(v -> {
                    ProductVariant pv = new ProductVariant();
                    pv.setSize(v.size());
                    pv.setColor(v.color());
                    pv.setConfiguration(v.configuration());
                    pv.setPrice(v.price());
                    pv.setStock(v.stock());
                    pv.setProduct(product);
                    return pv;
                }).toList();

        product.setVariants(variants);

        return product;
    }

    @Override
    public ProductDocument toDocument(Product product) {
        return ProductDocument.builder()
                .productId(product.getProductId())
                .sku(product.getSku())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .variantDocumentList(mapVariants(product.getVariants()))
                .build();
    }

    private List<VariantDocument> mapVariants(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            return List.of();
        }

        return variants.stream()
                .map(this::toVariantDocument)
                .toList();
    }

    private VariantDocument toVariantDocument(ProductVariant variant) {
        return VariantDocument.builder()
                .size(variant.getSize())
                .color(variant.getColor())
                .configuration(variant.getConfiguration())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .build();
    }
}
