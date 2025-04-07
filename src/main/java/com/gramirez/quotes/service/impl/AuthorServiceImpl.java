package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.exception.AuthorNotFoundException;
import com.gramirez.quotes.mapper.AuthorMapper;
import com.gramirez.quotes.model.Author;
import com.gramirez.quotes.repository.AuthorRepository;
import com.gramirez.quotes.service.AuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public List<AuthorDTO> findAll(Pageable pageable) {
        Page<Author> authorsPage = authorRepository.findAll(pageable);
        return authorsPage.stream().map(authorMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public AuthorDTO findById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        return authorMapper.toDTO(author);
    }

    @Override
    @Transactional
    public AuthorDTO create(AuthorDTO authorDTO) {
        Author author = authorMapper.toEntity(authorDTO);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toDTO(savedAuthor);
    }

    @Override
    @Transactional
    public AuthorDTO update(Long id, AuthorDTO authorDTO) {
        Author existingAuthor = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Author not found with id " + id));
        existingAuthor.setName(authorDTO.getName());

        Author updatedAuthor = authorRepository.save(existingAuthor);
        return authorMapper.toDTO(updatedAuthor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException("Author not found with id " + id);
        }
        authorRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Author findOrCreateByName(String name) {
        if (name ==  null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }

        Optional<Author> existingAuthor = authorRepository.findByNameIgnoreCase(name);
        if (existingAuthor.isPresent()) {
            return existingAuthor.get();
        } else {
            Author newAuthor = new Author();
            newAuthor.setName(name.trim());
            return  authorRepository.save(newAuthor);
        }

    }
}
