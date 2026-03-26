package com.web.demo.controls;

import com.product.dtos.ProductRequest;
import com.product.dtos.ProductResponse;
import com.product.dtos.ProductSearchRequest;
import com.web.demo.records.ProductDto;
import com.web.demo.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponse createSingleProduct(@RequestBody ProductRequest request) {
        return productService.createSingleProduct(request);
    }

    @PostMapping("bulkCreate")
    public String createBulkProducts(@RequestBody List<ProductRequest> requests) {
        return productService.createBulkProducts(requests);
    }

    @GetMapping("all")
    public List<ProductRequest> findAll() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    public ProductRequest getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductRequest> patchProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequest updatedData) {
        ProductRequest productRequest = productService.patchProduct(productId, updatedData);
        return ResponseEntity.ok(productRequest);
    }

    @GetMapping("colors")
    public List<String> availableColors() {
        return List.of("red", "green", "blue", "gold", "purple", "white", "gray", "black");
    }

    @GetMapping("list")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/byId/{id}")
    public ProductDto getProductByIdJsonFile(@PathVariable Long id) {
        return productService.getProductByIdJsonFile(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @PostMapping("/search")
    public List<ProductResponse> searchProducts(@RequestBody ProductSearchRequest productSearchRequest) {
        return productService.searchProducts(productSearchRequest);
    }

    @GetMapping
    public Page<ProductResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAll(page, size);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @PostMapping("/bulk")
    public List<ProductResponse> getProducts(
            @RequestBody Set<Long> ids) {

        return productService.getProductsByIds(ids);
    }
}

