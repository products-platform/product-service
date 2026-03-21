package com.web.demo.services;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.product.dtos.ProductElasticeSearchRequest;
import com.web.demo.documents.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchRepositoryCustom {

    private final ElasticsearchOperations operations;

    @Override
    public Page<ProductDocument> search(ProductElasticeSearchRequest request) {

        Query query = buildQuery(request);

        Pageable pageable = PageRequest.of(
                request.page(),
                request.pageSize()
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .withSort(getSortOptions(request)) // ✅ FIXED
                .build();

        SearchHits<ProductDocument> hits =
                operations.search(nativeQuery, ProductDocument.class);

        List<ProductDocument> content = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }

    // =========================
    // QUERY BUILDER
    // =========================
    private Query buildQuery(ProductElasticeSearchRequest req) {

        return Query.of(q -> q.bool(b -> {

            // 🔍 Full-text search
            if (hasText(req.keyword())) {
                b.must(m -> m.multiMatch(mm -> mm
                        .query(req.keyword())
                        .fields("name", "description")
                        .fuzziness("AUTO")
                ));
            }

            applyFilters(b, req);
            applyVariantFilters(b, req);

            return b;
        }));
    }

    private void applyFilters(
            co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
            ProductElasticeSearchRequest req) {

        if (hasText(req.brand())) {
            b.filter(f -> f.term(t -> t.field("brand").value(req.brand())));
        }

        if (hasText(req.category())) {
            b.filter(f -> f.term(t -> t.field("category").value(req.category())));
        }

        /*if (req.minPrice() != null || req.maxPrice() != null) {
            b.filter(f -> f.range(r -> r
                    .field("price")
                    .gte(req.minPrice() != null ? req.minPrice() : null)
                    .lte(req.maxPrice() != null ? req.maxPrice() : null)
            ));
        }*/
    }

    private void applyVariantFilters(
            co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
            ProductElasticeSearchRequest req) {

        boolean hasVariant =
                hasText(req.variantColor()) || hasText(req.variantSize());

        if (!hasVariant) return;

        b.filter(f -> f.nested(n -> n
                .path("variants")
                .query(nq -> nq.bool(nb -> {

                    if (hasText(req.variantColor())) {
                        nb.must(m -> m.term(t -> t
                                .field("variants.color")
                                .value(req.variantColor())
                        ));
                    }

                    if (hasText(req.variantSize())) {
                        nb.must(m -> m.term(t -> t
                                .field("variants.size")
                                .value(req.variantSize())
                        ));
                    }

                    return nb;
                }))
        ));
    }

    // =========================
    // SORTING (FIXED ✅)
    // =========================
    private List<SortOptions> getSortOptions(ProductElasticeSearchRequest req) {

        if (!hasText(req.sortBy())) {
            return List.of(); // no sorting
        }

        SortOrder order = "desc".equalsIgnoreCase(req.sortDirection())
                ? SortOrder.Desc
                : SortOrder.Asc;

        return List.of(
                SortOptions.of(s -> s
                        .field(f -> f
                                .field(req.sortBy())
                                .order(order)
                        )
                )
        );
    }

    private boolean hasText(String val) {
        return val != null && !val.isBlank();
    }
}
