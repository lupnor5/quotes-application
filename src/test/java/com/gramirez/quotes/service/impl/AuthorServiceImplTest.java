package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.exception.AuthorNotFoundException;
import com.gramirez.quotes.mapper.AuthorMapper;
import com.gramirez.quotes.model.Author;
import com.gramirez.quotes.repository.AuthorRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author1;
    private Author author2;
    private AuthorDTO authorDTO1;
    private AuthorDTO authorDTO2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup test data
        author1 = new Author();
        author1.setId(1L);
        author1.setName("Gabriel García Márquez");

        author2 = new Author();
        author2.setId(2L);
        author2.setName("Jorge Luis Borges");

        authorDTO1 = new AuthorDTO();
        authorDTO1.setId(1L);
        authorDTO1.setName("Gabriel García Márquez");

        authorDTO2 = new AuthorDTO();
        authorDTO2.setId(2L);
        authorDTO2.setName("Jorge Luis Borges");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findAll_shouldReturnAllAuthors() {
        // Arrange
        List<Author> authors = Arrays.asList(author1, author2);
        Page<Author> authorPage = new PageImpl<>(authors);

        when(authorRepository.findAll(pageable)).thenReturn(authorPage);
        when(authorMapper.toDTO(author1)).thenReturn(authorDTO1);
        when(authorMapper.toDTO(author2)).thenReturn(authorDTO2);

        // Act
        List<AuthorDTO> result = authorService.findAll(pageable);

        // Assert
        assertEquals(2, result.size());
        assertEquals(authorDTO1, result.get(0));
        assertEquals(authorDTO2, result.get(1));
        verify(authorRepository).findAll(pageable);
        verify(authorMapper, times(2)).toDTO(any(Author.class));
    }

    @Test
    void findById_whenAuthorExists_shouldReturnAuthor() {
        // Arrange
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(authorMapper.toDTO(author1)).thenReturn(authorDTO1);

        // Act
        AuthorDTO result = authorService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(authorDTO1, result);
        verify(authorRepository).findById(1L);
        verify(authorMapper).toDTO(author1);
    }

    @Test
    void findById_whenAuthorDoesNotExist_shouldThrowException() {
        // Arrange
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.findById(999L);
        });
        verify(authorRepository).findById(999L);
        verify(authorMapper, never()).toDTO(any(Author.class));
    }

    @Test
    void create_shouldReturnCreatedAuthor() {
        // Arrange
        when(authorMapper.toEntity(authorDTO1)).thenReturn(author1);
        when(authorRepository.save(author1)).thenReturn(author1);
        when(authorMapper.toDTO(author1)).thenReturn(authorDTO1);

        // Act
        AuthorDTO result = authorService.create(authorDTO1);

        // Assert
        assertNotNull(result);
        assertEquals(authorDTO1, result);
        verify(authorMapper).toEntity(authorDTO1);
        verify(authorRepository).save(author1);
        verify(authorMapper).toDTO(author1);
    }

    @Test
    void update_whenAuthorExists_shouldReturnUpdatedAuthor() {
        // Arrange
        AuthorDTO updateDTO = new AuthorDTO();
        updateDTO.setId(1L);
        updateDTO.setName("Gabriel García Márquez (Updated)");

        Author updatedAuthor = new Author();
        updatedAuthor.setId(1L);
        updatedAuthor.setName("Gabriel García Márquez (Updated)");

        AuthorDTO updatedDTO = new AuthorDTO();
        updatedDTO.setId(1L);
        updatedDTO.setName("Gabriel García Márquez (Updated)");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(authorRepository.save(any(Author.class))).thenReturn(updatedAuthor);
        when(authorMapper.toDTO(updatedAuthor)).thenReturn(updatedDTO);

        // Act
        AuthorDTO result = authorService.update(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedDTO, result);
        assertEquals("Gabriel García Márquez (Updated)", result.getName());
        verify(authorRepository).findById(1L);
        verify(authorRepository).save(any(Author.class));
        verify(authorMapper).toDTO(updatedAuthor);
    }

    @Test
    void update_whenAuthorDoesNotExist_shouldThrowException() {
        // Arrange
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.update(999L, authorDTO1);
        });
        verify(authorRepository).findById(999L);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void delete_whenAuthorExists_shouldDeleteAuthor() {
        // Arrange
        when(authorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(authorRepository).deleteById(1L);

        // Act
        authorService.delete(1L);

        // Assert
        verify(authorRepository).existsById(1L);
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void delete_whenAuthorDoesNotExist_shouldThrowException() {
        // Arrange
        when(authorRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.delete(999L);
        });
        verify(authorRepository).existsById(999L);
        verify(authorRepository, never()).deleteById(anyLong());
    }

    @Test
    void findOrCreateByName_whenAuthorExists_shouldReturnExistingAuthor() {
        // Arrange
        String authorName = "Gabriel García Márquez";
        when(authorRepository.findByNameIgnoreCase(authorName)).thenReturn(Optional.of(author1));

        // Act
        Author result = authorService.findOrCreateByName(authorName);

        // Assert
        assertNotNull(result);
        assertEquals(author1, result);
        verify(authorRepository).findByNameIgnoreCase(authorName);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void findOrCreateByName_whenAuthorDoesNotExist_shouldCreateAndReturnNewAuthor() {
        // Arrange
        String authorName = "Isabel Allende";
        Author newAuthor = new Author();
        newAuthor.setName(authorName);

        Author savedAuthor = new Author();
        savedAuthor.setId(3L);
        savedAuthor.setName(authorName);

        when(authorRepository.findByNameIgnoreCase(authorName)).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        // Act
        Author result = authorService.findOrCreateByName(authorName);

        // Assert
        assertNotNull(result);
        assertEquals(savedAuthor, result);
        assertEquals(3L, result.getId());
        assertEquals(authorName, result.getName());
        verify(authorRepository).findByNameIgnoreCase(authorName);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void findOrCreateByName_whenNameIsNull_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authorService.findOrCreateByName(null);
        });
        verify(authorRepository, never()).findByNameIgnoreCase(any());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void findOrCreateByName_whenNameIsEmpty_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authorService.findOrCreateByName("  ");
        });
        verify(authorRepository, never()).findByNameIgnoreCase(any());
        verify(authorRepository, never()).save(any(Author.class));
    }

}