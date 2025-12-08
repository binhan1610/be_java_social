package com.pokemonreview.api.service;

import com.pokemonreview.api.models.DataEntity;
import com.pokemonreview.api.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository dataRepository;

    public DataEntity getLastByTopic(String topic) {
        return dataRepository.findFirstByTopicOrderByIdDesc(topic);
    }

    public List<DataEntity> getPaged(int limit, int offset, String topic) {
        return dataRepository.getDataWithLimitOffset(limit, offset, topic);
    }

    public List<DataEntity> getByTimeRange(long from, long to, String  topic) {
        return dataRepository.findByIdBetweenAndTopicOrderByIdAsc(from, to, topic);
    }

    public DataEntity saveData(DataEntity entity) {
        return dataRepository.save(entity);
    }
}
