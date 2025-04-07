package com.gramirez.quotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.exception.AuthorNotFoundException;
import com.gramirez.quotes.exception.GlobalExceptionHandler;
import com.gramirez.quotes.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthorControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AuthorController authorController;

    @Mock
    private AuthorService authorService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private AuthorDTO authorDTO1;
    private AuthorDTO authorDTO2;
    private List<AuthorDTO> authorList;

    @BeforeEach
    void setUp() {

        openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        authorDTO1 = new AuthorDTO();
        authorDTO1.setId(1L);
        authorDTO1.setName("Albert Einstein");

        authorDTO2 = new AuthorDTO();
        authorDTO2.setId(2L);
        authorDTO2.setName("Marie Curie");


        authorList = Arrays.asList(authorDTO1, authorDTO2);
    }

    @Test
    void getAllAuthors_ShouldReturnListOfAuthors() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 60, Sort.by(Sort.Direction.DESC, "id"));
        when(authorService.findAll(any(Pageable.class))).thenReturn(authorList);

        // When & Then
        mockMvc.perform(get("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Albert Einstein")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Marie Curie")));

        verify(authorService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllAuthors_WithCustomPagingAndSorting_ShouldReturnListOfAuthors() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"));
        when(authorService.findAll(any(Pageable.class))).thenReturn(authorList);

        // When & Then
        mockMvc.perform(get("/api/authors")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(authorService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAuthorById_WhenAuthorExists_ShouldReturnAuthor() throws Exception {
        // Given
        when(authorService.findById(1L)).thenReturn(authorDTO1);

        // When & Then
        mockMvc.perform(get("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Albert Einstein")));

        verify(authorService, times(1)).findById(1L);
    }

    @Test
    void getAuthorById_WhenAuthorDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(authorService.findById(999L)).thenThrow(new AuthorNotFoundException("Author not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/authors/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).findById(999L);
    }

    @Test
    void createAuthor_ShouldReturnCreatedAuthor() throws Exception {
        // Given
        AuthorDTO newAuthorDTO = new AuthorDTO();
        newAuthorDTO.setName("Isaac Newton");


        AuthorDTO createdAuthorDTO = new AuthorDTO();
        createdAuthorDTO.setId(3L);
        createdAuthorDTO.setName("Isaac Newton");


        when(authorService.create(any(AuthorDTO.class))).thenReturn(createdAuthorDTO);

        // When & Then
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Isaac Newton")));

        verify(authorService, times(1)).create(any(AuthorDTO.class));
    }

    @Test
    void updateAuthor_WhenAuthorExists_ShouldReturnUpdatedAuthor() throws Exception {
        // Given
        AuthorDTO updateAuthorDTO = new AuthorDTO();
        updateAuthorDTO.setName("Albert Einstein");


        AuthorDTO updatedAuthorDTO = new AuthorDTO();
        updatedAuthorDTO.setId(1L);
        updatedAuthorDTO.setName("Albert Einstein");


        when(authorService.update(eq(1L), any(AuthorDTO.class))).thenReturn(updatedAuthorDTO);

        // When & Then
        mockMvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAuthorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Albert Einstein")));

        verify(authorService, times(1)).update(eq(1L), any(AuthorDTO.class));
    }

    @Test
    void updateAuthor_WhenAuthorDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        AuthorDTO updateAuthorDTO = new AuthorDTO();
        updateAuthorDTO.setName("Unknown Author");

        when(authorService.update(eq(999L), any(AuthorDTO.class)))
                .thenThrow(new AuthorNotFoundException("Author not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/authors/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAuthorDTO)))
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).update(eq(999L), any(AuthorDTO.class));
    }

    @Test
    void deleteAuthor_WhenAuthorExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(authorService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authorService, times(1)).delete(1L);
    }

    @Test
    void deleteAuthor_WhenAuthorDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new AuthorNotFoundException("Author not found with id: 999"))
                .when(authorService).delete(999L);

        // When & Then
        mockMvc.perform(delete("/api/authors/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).delete(999L);
    }
}