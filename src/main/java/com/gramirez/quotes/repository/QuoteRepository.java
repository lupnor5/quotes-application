package com.gramirez.quotes.repository;

import com.gramirez.quotes.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//TODO - Add integration tests for it
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @Query(value = """

            WITH frequency_map AS (
             SELECT length(text) AS leng_text, count(*) AS frequency
             FROM quotes
             WHERE length(text) <= :maxLength
             GROUP BY length(text)
           ),
           pairs AS (
             SELECT a.leng_text AS len_a, b.leng_text AS len_b,
                    a.frequency AS freq_a, b.frequency AS freq_b,
                    CASE
                      WHEN a.leng_text = b.leng_text THEN (a.frequency * (b.frequency - 1)) / 2
                      ELSE a.frequency * b.frequency
                    END AS pair_count
             FROM frequency_map a
             JOIN frequency_map b
             ON a.leng_text + b.leng_text <= :maxLength
             AND a.leng_text <= b.leng_text
           )
           SELECT SUM(pair_count) AS total_possible_pairs
           FROM pairs

           """, nativeQuery = true)
    long countPossiblePairs(@Param("maxLength") int maxLength);


}