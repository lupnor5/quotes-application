package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotePairServiceImplTest {

    @Mock
    private QuoteRepository quoteRepository;

    @InjectMocks
    private QuotePairServiceImpl quotePairService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void countPairsWithMaxLength_shouldCallRepositoryAndCountValidPairs() {
        int maxLength = 100;
        Map<Integer, Long> lengthFrequency = new HashMap<>();
        lengthFrequency.put(30, 5L);
        lengthFrequency.put(40, 3L);
        lengthFrequency.put(60, 2L);

        when(quoteRepository.getLengthFrequencyMap(maxLength)).thenReturn(lengthFrequency);

        Long result = quotePairService.countPairsWithMaxLength(maxLength);

        verify(quoteRepository).getLengthFrequencyMap(maxLength);
        assertEquals(44L, result); // Expected value should match what countValidPairs would return
    }

    @Test
    void countValidPairs_withEmptyMap_shouldReturnZero() {
        Map<Integer, Long> emptyMap = new HashMap<>();
        int maxLength = 100;

        long result = quotePairService.countValidPairs(emptyMap, maxLength);

        assertEquals(0L, result);
    }

    @Test
    void countValidPairs_withSingleEntry_shouldCalculateCombinationsCorrectly() {
        Map<Integer, Long> singleEntryMap = new HashMap<>();
        singleEntryMap.put(40, 5L);
        int maxLength = 100;

        long result = quotePairService.countValidPairs(singleEntryMap, maxLength);

        // For 5 elements, the number of combinations is (5 * 4) / 2 = 10
        assertEquals(10L, result);
    }

    @Test
    void countValidPairs_withMultipleEntriesAllBelowHalfMaxLength_shouldCalculateAllCombinations() {
        Map<Integer, Long> multiEntryMap = new HashMap<>();
        multiEntryMap.put(20, 3L); // 3 quotes of length 20
        multiEntryMap.put(30, 4L); // 4 quotes of length 30
        multiEntryMap.put(40, 2L); // 2 quotes of length 40
        int maxLength = 100;

        long result = quotePairService.countValidPairs(multiEntryMap, maxLength);

        // Combinations of same size:
        // - 20: (3*2)/2 = 3
        // - 30: (4*3)/2 = 6
        // - 40: (2*1)/2 = 1
        // Combinations between different sizes:
        // - 20 with 30: 3*4 = 12
        // - 20 with 40: 3*2 = 6
        // - 30 with 40: 4*2 = 8
        // Total: 3 + 6 + 1 + 12 + 6 + 8 = 36
        assertEquals(36L, result);
    }

    @Test
    void countValidPairs_withSomePairsOverMaxLength_shouldExcludeThosePairs() {
        Map<Integer, Long> mixedLengthMap = new HashMap<>();
        mixedLengthMap.put(20, 3L);  // 3 quotes of length 20
        mixedLengthMap.put(40, 2L);  // 2 quotes of length 40
        mixedLengthMap.put(60, 4L);  // 4 quotes of length 60
        int maxLength = 70;          // Only allows combinations up to 70

        long result = quotePairService.countValidPairs(mixedLengthMap, maxLength);

        // Combinations of same size:
        // - 20 * 2 : (3*2)/2 = 3
        // We don't count 60 with 60 because 60+60 > 70
        // Combinations between different sizes:
        // - 20 with 40: 3*2 = 6
        // - 20 with 60: not valid (20+60 > 70), so not counted
        // - 40 with 60: not valid (40+60 > 70), so not counted
        // Total: 3 + 6 = 9
        assertEquals(9L, result);
    }

    @Test
    void countValidPairs_withLongQuotesAndSmallMaxLength_shouldReturnZero() {
        Map<Integer, Long> longQuotesMap = new HashMap<>();
        longQuotesMap.put(60, 5L);  // 5 quotes of length 60
        longQuotesMap.put(70, 3L);  // 3 quotes of length 70
        int maxLength = 50;         // Doesn't allow any combination (all are > maxLength/2)

        long result = quotePairService.countValidPairs(longQuotesMap, maxLength);

        assertEquals(0L, result);
    }

    @Test
    void countValidPairs_withExactlyHalfMaxLengthQuotes_shouldCountCorrectly() {
        Map<Integer, Long> exactHalfMap = new HashMap<>();
        exactHalfMap.put(50, 4L);  // 4 quotes of length exactly half of maxLength
        int maxLength = 100;       // Maximum allowed

        long result = quotePairService.countValidPairs(exactHalfMap, maxLength);

        // For 4 elements, the number of combinations is (4 * 3) / 2 = 6
        assertEquals(6L, result);
    }

    @Test
    void countValidPairs_withEdgeCaseValues_shouldHandleCorrectly() {
        Map<Integer, Long> edgeCaseMap = new HashMap<>();
        edgeCaseMap.put(1, 1000000L);  // 1,000,000 quotes of length 1
        int maxLength = 2;            // Very small max allowed

        long result = quotePairService.countValidPairs(edgeCaseMap, maxLength);

        // For 1,000,000 elements, the number of combinations is (1000000 * 999999) / 2 = 499999500000
        assertEquals(499999500000L, result);
    }

    @Test
    void countValidPairs_withMixedFrequencies_shouldCalculateCorrectly() {
        Map<Integer, Long> mixedFreqMap = new HashMap<>();
        mixedFreqMap.put(10, 1L);    // 1 quote of length 10
        mixedFreqMap.put(15, 2L);    // 2 quotes of length 15
        mixedFreqMap.put(25, 3L);    // 3 quotes of length 25
        mixedFreqMap.put(30, 1L);    // 1 quote of length 30
        int maxLength = 50;          // Maximum allowed

        long result = quotePairService.countValidPairs(mixedFreqMap, maxLength);

        // Combinations of same size:
        // - 10: 0 (only one)
        // - 15: (2*1)/2 = 1
        // - 25: (3*2)/2 = 3
        // - 30: 0 (only one)
        // Combinations between different sizes:
        // - 10 with 15: 1*2 = 2
        // - 10 with 25: 1*3 = 3
        // - 10 with 30: 1*1 = 1
        // - 15 with 25: 2*3 = 6
        // - 15 with 30: 2*1 = 2
        // - 25 with 30: not valid (25+30 > 50), so not counted
        // Total: 1 + 3 + 2 + 3 + 1 + 6 + 2 = 18
        assertEquals(18L, result);
    }
}