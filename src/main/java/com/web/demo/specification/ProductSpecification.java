package com.web.demo.specification;

import com.product.dtos.ProductSearchRequest;
import com.web.demo.models.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> search(ProductSearchRequest productSearchRequest) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productSearchRequest.category() != null) {
                predicates.add(
                        cb.equal(root.get("category"), productSearchRequest.category())
                );
            }

            if (productSearchRequest.brand() != null) {
                predicates.add(
                        cb.equal(root.get("brand"), productSearchRequest.brand())
                );
            }

            if (productSearchRequest.minPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("price"), productSearchRequest.minPrice())
                );
            }

            if (productSearchRequest.maxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("price"), productSearchRequest.maxPrice())
                );
            }

            if (productSearchRequest.active() != null) {
                predicates.add(
                        cb.equal(root.get("active"), productSearchRequest.active())
                );
            }

            if (productSearchRequest.keyword() != null && !productSearchRequest.keyword().isBlank()) {
                String keyword = "%" + productSearchRequest.keyword().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("name")), keyword),
                                cb.like(cb.lower(root.get("description")), keyword),
                                cb.like(cb.lower(root.get("sku")), keyword)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
