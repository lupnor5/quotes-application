package com.gramirez.quotes.controller;

import com.gramirez.quotes.dto.AuthorDTO;
import com.gramirez.quotes.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors", description = "API for managing authors of quotes")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @Operation(
            summary = "Get all authors",
            description = "Retrieve a paginated list of all authors with sorting options",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "60"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: property,direction. Default is id,desc",
                            example = "name,asc")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of authors",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthorDTO.class))
    )
    public ResponseEntity<List<AuthorDTO>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "60") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC: Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return ResponseEntity.ok(authorService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get author by ID",
            description = "Retrieve a specific author by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author found",
                    content = @Content(schema = @Schema(implementation = AuthorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found"
            )
    })
    public ResponseEntity<AuthorDTO> getAuthorById(
            @Parameter(description = "ID of the author to be retrieved", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create a new author",
            description = "Add a new author to the system"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Author created successfully",
            content = @Content(schema = @Schema(implementation = AuthorDTO.class))
    )
    public ResponseEntity<AuthorDTO> createAuthor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Author details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthorDTO.class))
            )
            @RequestBody AuthorDTO authorDTO) {
        return new ResponseEntity<>(authorService.create(authorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an author",
            description = "Update an existing author's information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author updated successfully",
                    content = @Content(schema = @Schema(implementation = AuthorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found"
            )
    })
    public ResponseEntity<AuthorDTO> updateAuthor(
            @Parameter(description = "ID of the author to be updated", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated author details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthorDTO.class))
            )
            @RequestBody AuthorDTO authorDTO) {
        return ResponseEntity.ok(authorService.update(id, authorDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an author",
            description = "Remove an author from the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Author deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found"
            )
    })
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(description = "ID of the author to be deleted", required = true, example = "1")
            @PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}