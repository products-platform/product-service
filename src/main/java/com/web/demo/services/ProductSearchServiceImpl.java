package com.web.demo.services;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.json.JsonData;
import com.product.dtos.*;
import com.web.demo.documents.ProductDocument;
import com.web.demo.mapper.ProductMapper;
import com.web.demo.repos.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductMapper productMapper;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public Page<ProductElasticSearchResponse> search(ProductElasticSearchRequest request) {
        return productSearchRepository.search(request)
                .map(this::map);
    }

    @Override
    public List<ProductRequest> searchMultiple(MultipleProductSearchRequest request) {
        NativeQuery query = buildQuery(
                request.keyword(),
                request.brands(),
                request.sizes(),
                request.colors(),
                request.minPrice(),
                request.maxPrice(),
                request.page(),
                request.sizeLimit(),
                request.sortBy()
        );

        return execute(query);
    }

    // ================= COMMON EXECUTOR =================
    private List<ProductRequest> execute(NativeQuery query) {
        return elasticsearchOperations.search(query, ProductDocument.class)
                .stream()
                .map(hit -> productMapper.mapToResponseFromDocument(hit.getContent()))
                .toList();
    }

    // ================= COMMON QUERY BUILDER =================
    private NativeQuery buildQuery(
            String keyword,
            List<String> brands,
            List<String> sizes,
            List<String> colors,
            Double minPrice,
            Double maxPrice,
            Integer page,
            Integer sizeLimit,
            String sortBy
    ) {

        return NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    applyKeyword(b, keyword);
                    applyBrandFilter(b, brands);
                    applyVariantFilter(b, sizes, colors);
                    applyPrice(b, minPrice, maxPrice);

                    return b;
                }))
                .withPageable(PageRequest.of(
                        page != null ? page : 0,
                        sizeLimit != null ? sizeLimit : 10
                ))
                .withSort(s -> applySorting(s, sortBy))
                .build();
    }

   /* private void applyPrice(BoolQuery.Builder b, Double minPrice, Double maxPrice) {
        b.filter(f -> f.nested(n -> n
                .path("variantDocumentList")
                .query(nq -> nq.bool(nb -> {
                    // price
                    if (minPrice != null || maxPrice != null) {
                        nb.must(m -> m.range(r -> r.number(num -> {
                            num.field("variantDocumentList.price");
                            if (minPrice != null) num.gte(minPrice);
                            if (maxPrice != null) num.lte(maxPrice);
                            return num;
                        })));
                    }

                    return nb;
                }))
        ));
    }*/

    private void applyPrice(BoolQuery.Builder b, Double minPrice, Double maxPrice) {

        b.filter(f -> f.nested(n -> n
                .path("variantDocumentList")
                .query(nq -> nq.bool(nb -> {

                    if (minPrice != null || maxPrice != null) {

                        nb.must(m -> m.range(r -> {
                            r.field("variantDocumentList.price");

                            if (minPrice != null) {
                                r.gte(JsonData.of(minPrice));
                            }
                            if (maxPrice != null) {
                                r.lte(JsonData.of(maxPrice));
                            }

                            return r;
                        }));
                    }

                    return nb;
                }))
        ));
    }

    private void applyBrandFilter(co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
                                  List<String> brands) {
        if (brands != null && !brands.isEmpty()) {
            b.filter(f -> f.terms(t -> t
                    .field("brand")
                    .terms(v -> v.value(
                            brands.stream().map(FieldValue::of).toList()
                    ))
            ));
        }
    }

    private void applyVariantFilter(
            co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
            List<String> sizes,
            List<String> colors) {
        if (sizes != null || colors != null) {
            b.filter(f -> f.nested(n -> n
                    .path("variantDocumentList")
                    .query(nq -> nq.bool(nb -> {
                        applySizeFilter(nb,sizes);
                        applyColorFilter(nb,colors);
                        return nb;
                    }))
            ));
        }
    }

    private void applyColorFilter(BoolQuery.Builder nb, List<String> colors) {
        // color
        if (colors != null && !colors.isEmpty()) {
            /*nb.must(m -> m.terms(t -> t
                    .field("variantDocumentList.color")
                    .terms(v -> v.value(
                            colors.stream().map(FieldValue::of).toList()
                    ))
            ));*/

            nb.must(m -> m.nested(n -> n
                    .path("variantDocumentList")
                    .query(q -> q.terms(t -> t
                            .field("variantDocumentList.color")
                            .terms(v -> v.value(
                                    colors.stream().map(FieldValue::of).toList()
                            ))
                    ))
                    .innerHits(i -> i)
            ));
        }
    }

    private void applySizeFilter(BoolQuery.Builder nb, List<String> sizes) {
        // size
        if (sizes != null && !sizes.isEmpty()) {
            nb.must(m -> m.terms(t -> t
                    .field("variantDocumentList.size")
                    .terms(v -> v.value(
                            sizes.stream().map(FieldValue::of).toList()
                    ))
            ));
        }
    }

    private co.elastic.clients.util.ObjectBuilder<
            co.elastic.clients.elasticsearch._types.SortOptions> applySorting(
            co.elastic.clients.elasticsearch._types.SortOptions.Builder s,
            String sortBy) {

        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            return s.field(f -> f
                    .field("variantDocumentList.price")
                    .order(SortOrder.Asc)
                    .nested(n -> n.path("variantDocumentList"))
            );
        }

        if ("priceDesc".equalsIgnoreCase(sortBy)) {
            return s.field(f -> f
                    .field("variantDocumentList.price")
                    .order(SortOrder.Desc)
                    .nested(n -> n.path("variantDocumentList"))
            );
        }

        return s; // ✅ return builder itself
    }


    @Override
    public List<ProductRequest> searchProducts(String keyword) {
        NativeQuery query = buildQuery(
                keyword,
                0,
                10,
                null
        );

        SearchHits<ProductDocument> searchHits =
                elasticsearchOperations.search(query, ProductDocument.class);

        return searchHits.stream()
                .map(hit -> productMapper.mapToResponseFromDocument(hit.getContent()))
                .toList();
    }

    // ================= COMMON QUERY BUILDER =================
    private NativeQuery buildQuery(
            String keyword,
            Integer page,
            Integer sizeLimit,
            String sortBy
    ) {

        return NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    applyKeyword(b, keyword);
                    return b;
                }))
                .withPageable(PageRequest.of(
                        page != null ? page : 0,
                        sizeLimit != null ? sizeLimit : 10
                ))
                //.withSort(s -> applySorting(s, sortBy))
                .build();
    }

    // ================= FILTER METHODS =================

    private void applyKeyword(co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder b,
                              String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            b.must(m -> m.multiMatch(mm -> mm
                    .fields("name", "brand", "category")
                    .query(keyword)
                    .fuzziness("AUTO")
            ));
        }
    }

    private ProductElasticSearchResponse map(ProductDocument doc) {
        return new ProductElasticSearchResponse(
                doc.getProductId(),
                doc.getName(),
                doc.getBrand(),
                doc.getCategory(),
                doc.getPrice(),
                doc.getVariantDocumentList().stream()
                        .map(v -> new VariantResponse(
                                v.getSize(),
                                v.getColor(),
                                v.getConfiguration(),
                                v.getPrice(),
                                10
                        ))
                        .toList()
        );
    }

}
