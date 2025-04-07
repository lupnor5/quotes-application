package com.gramirez.quotes.mapper;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class AuthorMapperTest {

    private AuthorMapper authorMapper;

    @BeforeEach
    void setUp() {
        authorMapper = new AuthorMapper();
    }

    @Test
    void toDTO_whenAuthorIsNull_shouldReturnNull() {
        // Act
        AuthorDTO result = authorMapper.toDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDTO_whenAuthorIsValid_shouldMapCorrectly() {
        // Arrange
        Author author = new Author();
        author.setId(1L);
        author.setName("Gabriel García Márquez");

        // Act
        AuthorDTO result = authorMapper.toDTO(author);

        // Assert
        assertNotNull(result);
        assertEquals(author.getId(), result.getId());
        assertEquals(author.getName(), result.getName());
    }

    @Test
    void toDTO_withQuotes_shouldNotIncludeQuotesInDTO() {
        // Arrange
        Author author = new Author();
        author.setId(1L);
        author.setName("Albert Camus");
        author.setQuotes(new HashSet<>());

        // Act
        AuthorDTO result = authorMapper.toDTO(author);

        // Assert
        assertNotNull(result);
        assertEquals(author.getId(), result.getId());
        assertEquals(author.getName(), result.getName());
        // La DTO no debería tener una propiedad para quotes según la definición
    }

    @Test
    void toEntity_whenDTOIsNull_shouldReturnNull() {
        // Act
        Author result = authorMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_whenDTOIsValid_shouldMapCorrectly() {
        // Arrange
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(2L);
        authorDTO.setName("Jorge Luis Borges");

        // Act
        Author result = authorMapper.toEntity(authorDTO);

        // Assert
        assertNotNull(result);
        assertEquals(authorDTO.getId(), result.getId());
        assertEquals(authorDTO.getName(), result.getName());
        assertNull(result.getQuotes()); // Al convertir de DTO a entidad, quotes debería ser null
    }

    @Test
    void bidirectionalMapping_shouldMaintainEquality() {
        // Arrange
        AuthorDTO initialDTO = new AuthorDTO();
        initialDTO.setId(3L);
        initialDTO.setName("Julio Cortázar");

        // Act
        Author entity = authorMapper.toEntity(initialDTO);
        AuthorDTO resultDTO = authorMapper.toDTO(entity);

        // Assert
        assertEquals(initialDTO.getId(), resultDTO.getId());
        assertEquals(initialDTO.getName(), resultDTO.getName());
    }

    @Test
    void toDTO_withNullFields_shouldHandleNullsGracefully() {
        // Arrange
        Author author = new Author();
        // No establecemos ID ni nombre, quedando como null

        // Act
        AuthorDTO result = authorMapper.toDTO(author);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }

    @Test
    void toEntity_withNullFields_shouldHandleNullsGracefully() {
        // Arrange
        AuthorDTO authorDTO = new AuthorDTO();
        // No establecemos ID ni nombre, quedando como null

        // Act
        Author result = authorMapper.toEntity(authorDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getQuotes());
    }
}