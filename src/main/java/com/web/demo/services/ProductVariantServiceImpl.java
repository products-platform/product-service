package com.web.demo.services;

import com.product.dtos.ProductVariantRequest;
import com.web.demo.models.Product;
import com.web.demo.models.ProductVariant;
import com.web.demo.repos.ProductRepository;
import com.web.demo.repos.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    public ProductVariantServiceImpl(ProductRepository productRepository,
                                     ProductVariantRepository variantRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
    }

    @Override
    public ProductVariant addVariant(Long productId, ProductVariantRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductVariant variant = new ProductVariant();
        variant.setSize(request.size());
        variant.setColor(request.color());
        variant.setConfiguration(request.configuration());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        variant.setProduct(product);

        return variantRepository.save(variant);
    }

    @Override
    public List<ProductVariant> getVariants(Long productId) {
        return variantRepository.findByProduct_ProductId(productId);
    }
}
