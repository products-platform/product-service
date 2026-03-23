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

    @Override
    public Product buildEntity(ProductRequest dto) {
        if (dto == null) return null;

        Product product = Product.builder()
                .sku(dto.sku())
                .name(dto.name())
                .brand(dto.brand())
                .category(dto.category())
                .active(true)
                .build();

        if (dto.variants() != null) {
            List<ProductVariant> variantList = dto.variants().stream()
                    .map(variantDto -> ProductVariant.builder()
                            .variantSku(variantDto.variantSku())
                            .size(variantDto.size())
                            .color(variantDto.color())
                            .configuration(variantDto.configuration())
                            .price(variantDto.price())
                            .weightInGrams(variantDto.weightInGrams())
                            .description(variantDto.description())
                            .active(true)
                            .product(product) // link variant to parent product
                            .build())
                    .toList();

            product.setVariants(variantList);
        }

        return product;
    }

    public ProductDocument toDocument(Product product) {
        if (product == null) return null;

        return ProductDocument.builder()
                .productId(product.getProductId())
                .sku(product.getSku())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
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
                .build();
    }
}
