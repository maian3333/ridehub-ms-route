package com.ridehub.route.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ridehub.route.domain.Station;
import com.ridehub.route.repository.StationRepository;
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
 * Spring Data Elasticsearch repository for the {@link Station} entity.
 */
public interface StationSearchRepository extends ElasticsearchRepository<Station, Long>, StationSearchRepositoryInternal {}

interface StationSearchRepositoryInternal {
    Page<Station> search(String query, Pageable pageable);

    Page<Station> search(Query query);

    @Async
    void index(Station entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StationSearchRepositoryInternalImpl implements StationSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StationRepository repository;

    StationSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Station> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Station> search(Query query) {
        SearchHits<Station> searchHits = elasticsearchTemplate.search(query, Station.class);
        List<Station> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Station entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Station.class);
    }
}
