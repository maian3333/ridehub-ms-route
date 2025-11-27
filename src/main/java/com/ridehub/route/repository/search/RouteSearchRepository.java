package com.ridehub.route.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ridehub.route.domain.Route;
import com.ridehub.route.repository.RouteRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Route} entity.
 */
public interface RouteSearchRepository extends ElasticsearchRepository<Route, Long>, RouteSearchRepositoryInternal {}

interface RouteSearchRepositoryInternal {
    Page<Route> search(String query, Pageable pageable);

    Page<Route> search(Query query);

    @Async
    void index(Route entity);

    @Async
    void deleteFromIndexById(Long id);
}

class RouteSearchRepositoryInternalImpl implements RouteSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final RouteRepository repository;

    RouteSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, RouteRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Route> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Route> search(Query query) {
        SearchHits<Route> searchHits = elasticsearchTemplate.search(query, Route.class);
        List<Route> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Route entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Route.class);
    }
}
