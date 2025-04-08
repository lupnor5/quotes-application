package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.repository.QuoteRepository;
import com.gramirez.quotes.service.QuotePairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuotePairServiceImplTest {

    @Mock
    private QuoteRepository quoteRepository;

    private QuotePairService quotePairService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        quotePairService = new QuotePairServiceImpl(quoteRepository);
    }

    @Test
    public void testCountPairsWithMaxLength_ReturnsCorrectCount() {
        int maxLength = 10;
        Long expectedCount = 5L;
        when(quoteRepository.countPossiblePairs(maxLength)).thenReturn(expectedCount);

        Long actualCount = quotePairService.countPairsWithMaxLength(maxLength);

        assertEquals(expectedCount, actualCount, "The count should match the value returned by the repository");
        verify(quoteRepository, times(1)).countPossiblePairs(maxLength);
    }

    @Test
    public void testCountPairsWithMaxLength_WithZeroLength() {
        int maxLength = 0;
        Long expectedCount = 0L;
        when(quoteRepository.countPossiblePairs(maxLength)).thenReturn(expectedCount);

        Long actualCount = quotePairService.countPairsWithMaxLength(maxLength);

        assertEquals(expectedCount, actualCount, "Should return 0 for maxLength of 0");
        verify(quoteRepository, times(1)).countPossiblePairs(maxLength);
    }

    @Test
    public void testCountPairsWithMaxLength_WithNegativeLength() {
        int maxLength = -5;
        Long expectedCount = 0L;
        when(quoteRepository.countPossiblePairs(maxLength)).thenReturn(expectedCount);

        Long actualCount = quotePairService.countPairsWithMaxLength(maxLength);

        assertEquals(expectedCount, actualCount, "Should handle negative maxLength properly");
        verify(quoteRepository, times(1)).countPossiblePairs(maxLength);
    }
}