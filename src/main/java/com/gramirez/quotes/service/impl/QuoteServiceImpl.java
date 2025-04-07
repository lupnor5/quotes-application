package com.gramirez.quotes.service.impl;


import com.gramirez.quotes.dto.CreateQuoteDTO;
import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.exception.QuoteNotFoundException;
import com.gramirez.quotes.mapper.QuoteMapper;
import com.gramirez.quotes.model.Author;
import com.gramirez.quotes.model.Quote;
import com.gramirez.quotes.repository.QuoteRepository;
import com.gramirez.quotes.service.AuthorService;
import com.gramirez.quotes.service.QuoteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final AuthorService authorService;
    private final QuoteMapper quoteMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public QuoteServiceImpl(QuoteRepository quoteRepository,
                            AuthorService authorService,
                            QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.authorService = authorService;
        this.quoteMapper = quoteMapper;
    }

    @Override
    public List<QuoteDTO> findAll(Pageable pageable) {
        Page<Quote> quotesPage = quoteRepository.findAll(pageable);
        return quotesPage.stream()
                .map(quoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuoteDTO findById(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException("Quote not found with Id:" + id));

        return quoteMapper.toDto(quote);
    }

    @Override
    @Transactional
    public QuoteDTO create(CreateQuoteDTO createQuoteDTO) {
        Author author  = authorService.findOrCreateByName(createQuoteDTO.getAuthor());

        Quote quote = new Quote();
        quote.setText(createQuoteDTO.getText());
        quote.setAuthor(author);
        quote.setCreatedAt(LocalDateTime.now());
        quote.setUpdatedAt(LocalDateTime.now());

        Quote savedQuote = quoteRepository.save(quote);
        return quoteMapper.toDto(savedQuote);
    }

    @Override
    @Transactional
    public QuoteDTO update(Long id, CreateQuoteDTO createQuoteDTO) {
        Quote existingQuote = quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException("Quote not found with Id:" + id));

        Author author = authorService.findOrCreateByName(createQuoteDTO.getAuthor());

        existingQuote.setText(createQuoteDTO.getText());
        existingQuote.setAuthor(author);
        existingQuote.setUpdatedAt(LocalDateTime.now());

        Quote savedQuote = quoteRepository.save(existingQuote);
        return quoteMapper.toDto(savedQuote);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!quoteRepository.existsById(id)) {
            throw new QuoteNotFoundException("Quote not found with Id:" + id);
        }

        quoteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Quote> createBatch(List<CreateQuoteDTO> quoteDTOs) {
        List<Quote> savedQuotes = new ArrayList<>(quoteDTOs.size());

        for (int i = 0; i < quoteDTOs.size(); i++) {
            CreateQuoteDTO dto = quoteDTOs.get(i);
            Quote quote = new Quote();
            Author author = authorService.findOrCreateByName(dto.getAuthor());
            quote.setText(dto.getText());
            quote.setAuthor(author);

            entityManager.persist(quote);
            savedQuotes.add(quote);

            if (i % 30 == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        return savedQuotes;
    }
}
