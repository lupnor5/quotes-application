package com.gramirez.quotes.mapper;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.model.Author;
import com.gramirez.quotes.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteMapperTest {

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private QuoteMapper quoteMapper;

    private Author author;
    private AuthorDTO authorDTO;
    private Quote quote;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {

        now = LocalDateTime.now();

        author = new Author();
        author.setId(1L);
        author.setName("Pablo Neruda");

        authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setName("Pablo Neruda");

        quote = new Quote();
        quote.setId(1L);
        quote.setText("Podrán cortar todas las flores, pero no podrán detener la primavera.");
        quote.setAuthor(author);
        quote.setCreatedAt(now);
        quote.setUpdatedAt(now);
    }

    @Test
    void toDto_whenQuoteIsNull_shouldReturnNull() {

        QuoteDTO result = quoteMapper.toDto(null);

        assertNull(result);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void toDto_whenQuoteIsValid_shouldMapCorrectly() {

        when(authorMapper.toDTO(author)).thenReturn(authorDTO);

        QuoteDTO result = quoteMapper.toDto(quote);

        assertNotNull(result);
        assertEquals(quote.getId(), result.getId());
        assertEquals(quote.getText(), result.getText());
        assertEquals(authorDTO, result.getAuthor());


        verify(authorMapper, times(1)).toDTO(author);
    }

    @Test
    void toDto_whenAuthorIsNull_shouldHandleNullAuthor() {

        quote.setAuthor(null);
        when(authorMapper.toDTO(null)).thenReturn(null);

        QuoteDTO result = quoteMapper.toDto(quote);

        assertNotNull(result);
        assertEquals(quote.getId(), result.getId());
        assertEquals(quote.getText(), result.getText());
        assertNull(result.getAuthor());

        verify(authorMapper, times(1)).toDTO(null);
    }

    @Test
    void toDto_withNullId_shouldHandleNullId() {

        quote.setId(null);
        when(authorMapper.toDTO(author)).thenReturn(authorDTO);

        QuoteDTO result = quoteMapper.toDto(quote);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(quote.getText(), result.getText());
        assertEquals(authorDTO, result.getAuthor());
    }

    @Test
    void toDto_withNullText_shouldHandleNullText() {

        quote.setText(null);
        when(authorMapper.toDTO(author)).thenReturn(authorDTO);

        QuoteDTO result = quoteMapper.toDto(quote);

        assertNotNull(result);
        assertEquals(quote.getId(), result.getId());
        assertNull(result.getText());
        assertEquals(authorDTO, result.getAuthor());
    }

    @Test
    void toDto_shouldNotIncludeTimestamps() {

        when(authorMapper.toDTO(author)).thenReturn(authorDTO);

        QuoteDTO result = quoteMapper.toDto(quote);

        assertNotNull(result);

        assertEquals(quote.getId(), result.getId());
        assertEquals(quote.getText(), result.getText());
        assertEquals(authorDTO, result.getAuthor());
    }

    @Test
    void toDto_whenAuthorMapperThrowsException_shouldPropagateException() {

        RuntimeException expectedException = new RuntimeException("Error al mapear autor");
        when(authorMapper.toDTO(author)).thenThrow(expectedException);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            quoteMapper.toDto(quote);
        });

        assertEquals(expectedException, exception);
    }
}