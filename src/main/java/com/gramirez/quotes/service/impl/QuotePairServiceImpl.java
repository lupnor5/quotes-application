package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.repository.QuoteRepository;
import com.gramirez.quotes.service.QuotePairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuotePairServiceImpl implements QuotePairService {

    private final QuoteRepository quoteRepository;

    @Autowired
    public QuotePairServiceImpl(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Override
    public Long countPairsWithMaxLength(int maxLength) {
        return quoteRepository.countPossiblePairs(maxLength).orElse(0L);
    }

}
