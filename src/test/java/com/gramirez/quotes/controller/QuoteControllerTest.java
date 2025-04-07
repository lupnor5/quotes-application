package com.gramirez.quotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.dto.CreateQuoteDTO;
import com.gramirez.quotes.dto.PairsCountDTO;
import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.exception.GlobalExceptionHandler;
import com.gramirez.quotes.exception.QuoteNotFoundException;
import com.gramirez.quotes.service.impl.QuotePairServiceImpl;
import com.gramirez.quotes.service.impl.QuoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class QuoteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuoteServiceImpl quoteService;

    @Mock
    private QuotePairServiceImpl quotePairService;

    @InjectMocks
    private QuoteController quoteController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuoteDTO quoteDTO1;
    private QuoteDTO quoteDTO2;
    private List<QuoteDTO> quoteList;
    private CreateQuoteDTO createQuoteDTO;
    private AuthorDTO authorDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(quoteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setName("Albert Einstein");

        quoteDTO1 = new QuoteDTO();
        quoteDTO1.setId(1L);
        quoteDTO1.setText("Imagination is more important than knowledge.");
        quoteDTO1.setAuthor(authorDTO);

        quoteDTO2 = new QuoteDTO();
        quoteDTO2.setId(2L);
        quoteDTO2.setText("The important thing is not to stop questioning.");
        quoteDTO2.setAuthor(authorDTO);

        quoteList = Arrays.asList(quoteDTO1, quoteDTO2);

        createQuoteDTO = new CreateQuoteDTO();
        createQuoteDTO.setText("Life is like riding a bicycle. To keep your balance, you must keep moving.");
        createQuoteDTO.setAuthor("Albert Einstein");
    }

    @Test
    void getAllQuotes_ShouldReturnListOfQuotes() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        when(quoteService.findAll(any(Pageable.class))).thenReturn(quoteList);

        // When & Then
        mockMvc.perform(get("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].text", is("Imagination is more important than knowledge.")))
                .andExpect(jsonPath("$[0].author.id", is(1)))
                .andExpect(jsonPath("$[0].author.name", is("Albert Einstein")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].text", is("The important thing is not to stop questioning.")));

        verify(quoteService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllQuotes_WithCustomPagingAndSorting_ShouldReturnListOfQuotes() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "text"));
        when(quoteService.findAll(any(Pageable.class))).thenReturn(quoteList);

        // When & Then
        mockMvc.perform(get("/api/quotes")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "text,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(quoteService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getQuoteById_WhenQuoteExists_ShouldReturnQuote() throws Exception {
        // Given
        when(quoteService.findById(1L)).thenReturn(quoteDTO1);

        // When & Then
        mockMvc.perform(get("/api/quotes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Imagination is more important than knowledge.")))
                .andExpect(jsonPath("$.author.id", is(1)))
                .andExpect(jsonPath("$.author.name", is("Albert Einstein")));

        verify(quoteService, times(1)).findById(1L);
    }

    @Test
    void getQuoteById_WhenQuoteDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(quoteService.findById(999L)).thenThrow(new QuoteNotFoundException("Quote not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/quotes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(quoteService, times(1)).findById(999L);
    }

    @Test
    void createQuote_ShouldReturnCreatedQuote() throws Exception {
        // Given
        QuoteDTO createdQuoteDTO = new QuoteDTO();
        createdQuoteDTO.setId(3L);
        createdQuoteDTO.setText("Life is like riding a bicycle. To keep your balance, you must keep moving.");
        createdQuoteDTO.setAuthor(authorDTO);

        when(quoteService.create(any(CreateQuoteDTO.class))).thenReturn(createdQuoteDTO);

        // When & Then
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createQuoteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.text", is("Life is like riding a bicycle. To keep your balance, you must keep moving.")))
                .andExpect(jsonPath("$.author.id", is(1)))
                .andExpect(jsonPath("$.author.name", is("Albert Einstein")));

        verify(quoteService, times(1)).create(any(CreateQuoteDTO.class));
    }

    @Test
    void updateQuote_WhenQuoteExists_ShouldReturnUpdatedQuote() throws Exception {
        // Given
        QuoteDTO updatedQuoteDTO = new QuoteDTO();
        updatedQuoteDTO.setId(1L);
        updatedQuoteDTO.setText("Imagination is more important than knowledge. For knowledge is limited.");
        updatedQuoteDTO.setAuthor(authorDTO);

        CreateQuoteDTO updateRequest = new CreateQuoteDTO();
        updateRequest.setText("Imagination is more important than knowledge. For knowledge is limited.");
        updateRequest.setAuthor("Albert Einstein");

        when(quoteService.update(eq(1L), any(CreateQuoteDTO.class))).thenReturn(updatedQuoteDTO);

        // When & Then
        mockMvc.perform(put("/api/quotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Imagination is more important than knowledge. For knowledge is limited.")))
                .andExpect(jsonPath("$.author.id", is(1)))
                .andExpect(jsonPath("$.author.name", is("Albert Einstein")));

        verify(quoteService, times(1)).update(eq(1L), any(CreateQuoteDTO.class));
    }

    @Test
    void updateQuote_WhenQuoteDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        CreateQuoteDTO updateRequest = new CreateQuoteDTO();
        updateRequest.setText("This is an updated quote");
        updateRequest.setAuthor("Unknown Author");

        when(quoteService.update(eq(999L), any(CreateQuoteDTO.class)))
                .thenThrow(new QuoteNotFoundException("Quote not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/quotes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(quoteService, times(1)).update(eq(999L), any(CreateQuoteDTO.class));
    }

    @Test
    void countPairsWithMaxLength_ShouldReturnPairsCount() throws Exception {
        // Given
        int maxLength = 100;
        Long pairsCount = 5L;

        PairsCountDTO pairsCountDTO = new PairsCountDTO();
        pairsCountDTO.setCount(pairsCount);
        pairsCountDTO.setMaxLength(maxLength);

        when(quotePairService.countPairsWithMaxLength(maxLength)).thenReturn(pairsCount);

        // When & Then
        mockMvc.perform(get("/api/quotes/pairs/count/{maxLength}", maxLength)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(5)))
                .andExpect(jsonPath("$.maxLength", is(100)));

        verify(quotePairService, times(1)).countPairsWithMaxLength(maxLength);
    }

    @Test
    void countPairsWithMaxLength_WhenInvalidMaxLength_ShouldReturnBadRequest() throws Exception {
        // Given
        int invalidMaxLength = -10;

        when(quotePairService.countPairsWithMaxLength(invalidMaxLength))
                .thenThrow(new IllegalArgumentException("Maximum length must be positive"));

        // When & Then
        mockMvc.perform(get("/api/quotes/pairs/count/{maxLength}", invalidMaxLength)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(quotePairService, times(1)).countPairsWithMaxLength(invalidMaxLength);
    }
}