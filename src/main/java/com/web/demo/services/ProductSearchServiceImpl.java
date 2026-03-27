package com.web.demo.services;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.product.dtos.ProductElasticSearchRequest;
import com.product.dtos.ProductElasticSearchResponse;
import com.product.dtos.ProductRequest;
import com.product.dtos.VariantResponse;
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
                                v.getStock()
                        ))
                        .toList()
        );
    }

}
