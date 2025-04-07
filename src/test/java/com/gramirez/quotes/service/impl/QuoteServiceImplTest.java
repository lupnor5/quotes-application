package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.dto.CreateQuoteDTO;
import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.exception.QuoteNotFoundException;
import com.gramirez.quotes.mapper.QuoteMapper;
import com.gramirez.quotes.model.Author;
import com.gramirez.quotes.model.Quote;
import com.gramirez.quotes.repository.QuoteRepository;
import com.gramirez.quotes.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceImplTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private QuoteMapper quoteMapper;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private Quote quote1;
    private Quote quote2;
    private Author author;
    private QuoteDTO quoteDTO1;
    private QuoteDTO quoteDTO2;
    private CreateQuoteDTO createQuoteDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        author = new Author();
        author.setId(1L);
        author.setName("Albert Einstein");


        quote1 = new Quote();
        quote1.setId(1L);
        quote1.setText("La imaginación es más importante que el conocimiento.");
        quote1.setAuthor(author);
        quote1.setCreatedAt(now);
        quote1.setUpdatedAt(now);

        quote2 = new Quote();
        quote2.setId(2L);
        quote2.setText("La vida es como montar en bicicleta. Para mantener el equilibrio, debes seguir moviéndote.");
        quote2.setAuthor(author);
        quote2.setCreatedAt(now);
        quote2.setUpdatedAt(now);

        quoteDTO1 = new QuoteDTO();
        quoteDTO1.setId(1L);
        quoteDTO1.setText("La imaginación es más importante que el conocimiento.");


        quoteDTO2 = new QuoteDTO();
        quoteDTO2.setId(2L);
        quoteDTO2.setText("La vida es como montar en bicicleta. Para mantener el equilibrio, debes seguir moviéndote.");


        createQuoteDTO = new CreateQuoteDTO();
        createQuoteDTO.setText("La imaginación es más importante que el conocimiento.");
        createQuoteDTO.setAuthor("Albert Einstein");
    }

    @Test
    void findAll_ShouldReturnListOfQuoteDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Quote> quotes = Arrays.asList(quote1, quote2);
        Page<Quote> quotePage = new PageImpl<>(quotes, pageable, quotes.size());

        when(quoteRepository.findAll(pageable)).thenReturn(quotePage);
        when(quoteMapper.toDto(quote1)).thenReturn(quoteDTO1);
        when(quoteMapper.toDto(quote2)).thenReturn(quoteDTO2);

        // Act
        List<QuoteDTO> result = quoteService.findAll(pageable);

        // Assert
        assertEquals(2, result.size());
        assertEquals(quoteDTO1, result.get(0));
        assertEquals(quoteDTO2, result.get(1));
        verify(quoteRepository).findAll(pageable);
        verify(quoteMapper, times(2)).toDto(any(Quote.class));
    }

    @Test
    void findById_WithExistingId_ShouldReturnQuoteDTO() {
        // Arrange
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote1));
        when(quoteMapper.toDto(quote1)).thenReturn(quoteDTO1);

        // Act
        QuoteDTO result = quoteService.findById(1L);

        // Assert
        assertEquals(quoteDTO1, result);
        verify(quoteRepository).findById(1L);
        verify(quoteMapper).toDto(quote1);
    }

    @Test
    void findById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(quoteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(QuoteNotFoundException.class, () -> {
            quoteService.findById(999L);
        });
        verify(quoteRepository).findById(999L);
    }

    @Test
    void create_ShouldReturnCreatedQuoteDTO() {
        // Arrange
        when(authorService.findOrCreateByName(anyString())).thenReturn(author);
        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> {
            Quote savedQuote = invocation.getArgument(0);
            savedQuote.setId(1L);
            return savedQuote;
        });
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(quoteDTO1);

        // Act
        QuoteDTO result = quoteService.create(createQuoteDTO);

        // Assert
        assertEquals(quoteDTO1, result);
        verify(authorService).findOrCreateByName("Albert Einstein");
        verify(quoteRepository).save(any(Quote.class));
        verify(quoteMapper).toDto(any(Quote.class));
    }

    @Test
    void update_WithExistingId_ShouldReturnUpdatedQuoteDTO() {
        // Arrange
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote1));
        when(authorService.findOrCreateByName(anyString())).thenReturn(author);
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote1);
        when(quoteMapper.toDto(quote1)).thenReturn(quoteDTO1);

        CreateQuoteDTO updateDTO = new CreateQuoteDTO();
        updateDTO.setText("Texto actualizado");
        updateDTO.setAuthor("Albert Einstein");

        // Act
        QuoteDTO result = quoteService.update(1L, updateDTO);

        // Assert
        assertEquals(quoteDTO1, result);
        verify(quoteRepository).findById(1L);
        verify(authorService).findOrCreateByName("Albert Einstein");
        verify(quoteRepository).save(quote1);
        verify(quoteMapper).toDto(quote1);
    }

    @Test
    void update_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(quoteRepository.findById(999L)).thenReturn(Optional.empty());

        CreateQuoteDTO updateDTO = new CreateQuoteDTO();
        updateDTO.setText("Texto actualizado");
        updateDTO.setAuthor("Albert Einstein");

        // Act & Assert
        assertThrows(QuoteNotFoundException.class, () -> {
            quoteService.update(999L, updateDTO);
        });
        verify(quoteRepository).findById(999L);
        verify(authorService, never()).findOrCreateByName(anyString());
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    void delete_WithExistingId_ShouldDeleteQuote() {
        // Arrange
        when(quoteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(quoteRepository).deleteById(1L);

        // Act
        quoteService.delete(1L);

        // Assert
        verify(quoteRepository).existsById(1L);
        verify(quoteRepository).deleteById(1L);
    }

    @Test
    void delete_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(quoteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(QuoteNotFoundException.class, () -> {
            quoteService.delete(999L);
        });
        verify(quoteRepository).existsById(999L);
        verify(quoteRepository, never()).deleteById(anyLong());
    }
}