package com.web.demo.mapper;

import com.product.dtos.ProductRequest;
import com.product.dtos.ProductVariantRequest;
import com.web.demo.documents.ProductDocument;
import com.web.demo.documents.VariantDocument;
import com.web.demo.models.Product;
import com.web.demo.models.ProductVariant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                    .map(this::toEntity)
                    .toList();

            product.setVariants(variantList);
        }

        return product;
    }

    public void patchProduct(Product product, ProductRequest updatedData) {
        // ------------------------
        // Product fields (partial)
        // ------------------------
        if (updatedData.name() != null) product.setName(updatedData.name());
        if (updatedData.brand() != null) product.setBrand(updatedData.brand());
        if (updatedData.category() != null) product.setCategory(updatedData.category());
        if (updatedData.active() != null) product.setActive(updatedData.active());

        // ------------------------
        // Variants
        // ------------------------
        if (updatedData.variants() != null) {
            Map<Long, ProductVariant> existingMap = product.getVariants().stream()
                    .collect(Collectors.toMap(ProductVariant::getId, v -> v));

            for (ProductVariantRequest incoming : updatedData.variants()) {

                if (incoming.variantId() != null && existingMap.containsKey(incoming.variantId())) {
                    // ✅ Update existing
                    ProductVariant existing = existingMap.get(incoming.variantId());
                    patchVariant(existing, incoming);
                } else {
                    // ✅ Create new
                    ProductVariant newVariant = toEntity(incoming);
                    newVariant.setProduct(product); // VERY IMPORTANT
                    product.getVariants().add(newVariant);
                }
            }
        }
    }

    @Override
    public ProductRequest mapToResponseFromDocument(ProductDocument content) {
        return new ProductRequest(
                content.getProductId(),
                content.getSku(),
                content.getName(),
                content.getBrand(),
                content.getCategory(),
                toVariantFromDocList(content.getVariantDocumentList()),
                null,
                null,
                null
        );
    }

    private List<ProductVariantRequest> toVariantFromDocList(List<VariantDocument> variantDocumentList) {
        return variantDocumentList.stream().map(this::toVariantFromDoc).toList();
    }

    private ProductVariantRequest toVariantFromDoc(VariantDocument variantDocument) {
        return new ProductVariantRequest(Long.valueOf(10),
                variantDocument.getSize(),
                variantDocument.getColor(),
                variantDocument.getConfiguration(),
                variantDocument.getPrice(),
                null,
                null,
                null,
                null,
                null
        );
    }

    // ------------------------
    // Variant partial update
    // ------------------------
    private void patchVariant(ProductVariant existing, ProductVariantRequest incoming) {
        if (incoming.variantSku() != null) existing.setVariantSku(incoming.variantSku());
        if (incoming.price() != null) existing.setPrice(incoming.price());
        if (incoming.size() != null) existing.setSize(incoming.size());
        if (incoming.color() != null) existing.setColor(incoming.color());
        if (incoming.configuration() != null) existing.setConfiguration(incoming.configuration());
        if (incoming.weightInGrams() != null) existing.setWeightInGrams(incoming.weightInGrams());
        if (incoming.description() != null) existing.setDescription(incoming.description());
    }

    // ------------------------
    // New variant mapping
    // ------------------------
    private ProductVariant toEntity(ProductVariantRequest incoming) {
        return ProductVariant.builder()
                .variantSku(incoming.variantSku())
                .size(incoming.size())
                .color(incoming.color())
                .configuration(incoming.configuration())
                .price(incoming.price())
                .weightInGrams(incoming.weightInGrams())
                .description(incoming.description())
                .active(true)
                .build();
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

    @Override
    public List<ProductRequest> toRecords(List<Product> products) {
        return products.stream().map(this::toRecord).toList();
    }

    // Entity -> Record
    @Override
    public ProductRequest toRecord(Product product) {
        if (product == null) return null;

        return new ProductRequest(
                product.getProductId(),
                product.getSku(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                toVariantRecordList(product.getVariants()),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static List<ProductVariantRequest> toVariantRecordList(List<ProductVariant> variants) {
        if (variants == null) return List.of();

        return variants.stream()
                .map(ProductMapperImpl::toVariantRecord)
                .toList();
    }

    public static ProductVariantRequest toVariantRecord(ProductVariant variant) {
        if (variant == null) return null;

        return new ProductVariantRequest(
                variant.getId(),
                variant.getSize(),
                variant.getColor(),
                variant.getConfiguration(),
                variant.getPrice(),
                variant.getWeightInGrams(),
                variant.getDescription(),
                variant.getVariantSku(),
                10,
                variant.getActive()
        );

    }
}
