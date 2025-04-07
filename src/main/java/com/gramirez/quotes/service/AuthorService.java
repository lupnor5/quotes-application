package com.gramirez.quotes.service;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.model.Author;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {
    List<AuthorDTO> findAll(Pageable pageable);
    AuthorDTO findById(Long id);
    AuthorDTO create(AuthorDTO authorDTO);
    AuthorDTO update(Long id, AuthorDTO authorDTO);
    void delete(Long id);
    Author findOrCreateByName(String name);
}
