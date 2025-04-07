package com.gramirez.quotes.repository;

import com.gramirez.quotes.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @Query(value = "select length(text) as lengthText, count(*) as count from quotes where length(text) <= :maxLength group by length(text)", nativeQuery = true)
    List<LengthCountProjection> getLengthFrequencies(@Param("maxLength") int maxLength);

    interface LengthCountProjection {
        Integer getLengthText();
        Long getCount();
    }

    default Map<Integer, Long> getLengthFrequencyMap(int maxLength) {
        return getLengthFrequencies(maxLength).stream()
                .collect(Collectors.toMap(
                        LengthCountProjection::getLengthText,
                        LengthCountProjection::getCount
                ));
    }
}
