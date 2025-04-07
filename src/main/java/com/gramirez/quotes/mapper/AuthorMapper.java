package com.gramirez.quotes.mapper;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
    public AuthorDTO toDTO(Author author) {
        if (author == null) return null;

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setName(author.getName());

        return authorDTO;
    }

    public Author toEntity(AuthorDTO authorDTO) {
        if (authorDTO == null) return null;

        Author author = new Author();
        author.setId(authorDTO.getId());
        author.setName(authorDTO.getName());

        return author;
    }
}
