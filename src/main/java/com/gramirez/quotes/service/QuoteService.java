package com.gramirez.quotes.service;

import com.gramirez.quotes.dto.CreateQuoteDTO;
import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.model.Quote;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuoteService {
    List<QuoteDTO> findAll(Pageable pageable);
    QuoteDTO findById(Long id);
    QuoteDTO create (CreateQuoteDTO createQuoteDTO);
    QuoteDTO update (Long id, CreateQuoteDTO createQuoteDTO);
    void delete(Long id);
    List<Quote> createBatch(List<CreateQuoteDTO> quoteDTOs);
}
