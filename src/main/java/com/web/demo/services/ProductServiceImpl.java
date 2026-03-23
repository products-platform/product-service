package com.web.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.product.dtos.ProductRequest;
import com.product.dtos.ProductResponse;
import com.product.dtos.ProductSearchRequest;
import com.product.exceptions.DuplicateResourceException;
import com.product.exceptions.ProductNotFoundException;
import com.product.exceptions.ResourceNotFoundException;
import com.web.demo.documents.ProductDocument;
import com.web.demo.mapper.ProductMapper;
import com.web.demo.models.Product;
import com.web.demo.producer.ProductEventProducer;
import com.web.demo.reader.JsonFileReader;
import com.web.demo.records.ProductDto;
import com.web.demo.repos.ProductRepository;
import com.web.demo.repos.ProductSearchRepository;
import com.web.demo.specification.ProductSpecification;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String FILE_NAME = "products.json";

    private final JsonFileReader jsonFileReader;

    private List<ProductDto> products;

    private final ProductRepository productRepository;
    private final ProductEventProducer producer;
    private final ProductMapper productMapper;
    private final ProductSearchRepository searchRepository;

    @PostConstruct
    public void loadProducts() {
        this.products = jsonFileReader.readListFromFile(
                FILE_NAME,
                new TypeReference<>() {
                }
        );
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return products;
    }

    @Override
    public ProductDto getProductByIdJsonFile(Long id) {
        return products.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id));
    }

    // ✅ CREATE PRODUCT
    //@CachePut(value = "products", key = "#result.id")
    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.buildEntity(request);
        // ✅ Save to DB
        productRepository.save(product);

        // Publish event after save
        //producer.publishProductCreated(product);

        // ✅ Index to Elasticsearch
        indexToElastic(product);
        return mapToResponse(product);
    }

    private void indexToElastic(Product product) {
        ProductDocument document = productMapper.toDocument(product);
        searchRepository.save(document);
    }

    @Override
    @CachePut(value = "products", key = "#id")
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.name());
        product.setBrand(request.brand());
        product.setCategory(request.category());

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setActive(false); // soft delete
    }

    @Override
    public List<ProductResponse> getProductsByIds(
            Set<Long> ids) {
        return productRepository.findAllById(ids)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> searchProducts(ProductSearchRequest productSearchRequest) {
        return productRepository.findAll(ProductSpecification.search(productSearchRequest))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void refreshUsers() {
        // reload database
    }

    @Cacheable(value = "products", key = "#category + ':' + #id")
    public Product getProduct(String category, Long id) {
        return null;
    }

    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public Product getUser(Long id) {
        return null;
    }

    @Cacheable(value = "users", key = "#id", condition = "#id > 10")
    public Product getUser1(Long id) {
        return null;
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getSku(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
