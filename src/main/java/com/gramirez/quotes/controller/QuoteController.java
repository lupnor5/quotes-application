package com.gramirez.quotes.controller;

import com.gramirez.quotes.dto.CreateQuoteDTO;
import com.gramirez.quotes.dto.PairsCountDTO;
import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.service.QuotePairService;
import com.gramirez.quotes.service.QuoteService;
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
@RequestMapping("/api/quotes")
@Tag(name = "Quotes", description = "API for managing and analyzing famous quotes")
public class QuoteController {

    private final QuoteService quoteService;
    private final QuotePairService quotePairService;

    @Autowired
    public QuoteController(QuoteService quoteService, QuotePairService quotePairService) {
        this.quoteService = quoteService;
        this.quotePairService = quotePairService;
    }

    @GetMapping
    @Operation(
            summary = "Get paginated quotes",
            description = "Retrieves a paginated and sorted list of all quotes in the system",
            parameters = {
                    @Parameter(name = "page", description = "Zero-based page index", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "10"),
                    @Parameter(name = "sort", description = "Sorting criteria in format: property,direction. Multiple sort params allowed",
                            example = "id,desc")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved quote list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = QuoteDTO.class))
    )
    public ResponseEntity<List<QuoteDTO>> getAllQuotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(quoteService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get quote by ID",
            description = "Retrieves a specific quote by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote found",
                    content = @Content(schema = @Schema(implementation = QuoteDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quote not found",
                    content = @Content
            )
    })
    public ResponseEntity<QuoteDTO> getQuoteById(
            @Parameter(description = "ID of the quote to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(quoteService.findById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create a new quote",
            description = "Adds a new quote to the system"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Quote created successfully",
            content = @Content(schema = @Schema(implementation = QuoteDTO.class))
    )
    public ResponseEntity<QuoteDTO> createQuote(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Quote creation data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateQuoteDTO.class))
            )
            @RequestBody CreateQuoteDTO createQuoteDTO) {
        return new ResponseEntity<>(quoteService.create(createQuoteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update existing quote",
            description = "Updates the content of an existing quote"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote updated successfully",
                    content = @Content(schema = @Schema(implementation = QuoteDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quote not found",
                    content = @Content
            )
    })
    public ResponseEntity<QuoteDTO> updateQuote(
            @Parameter(description = "ID of the quote to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated quote data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateQuoteDTO.class)))
            @RequestBody CreateQuoteDTO createQuoteDTO) {
        return ResponseEntity.ok(quoteService.update(id, createQuoteDTO));
    }

    @GetMapping("/pairs/count/{maxLength}")
    @Operation(
            summary = "Count compatible quote pairs",
            description = "Calculates the number of unique quote pairs where the combined text length " +
                    "is less than or equal to the specified maximum length"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pair count calculated successfully",
                    content = @Content(schema = @Schema(implementation = PairsCountDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid maximum length parameter",
                    content = @Content
            )
    })
    public ResponseEntity<PairsCountDTO> countPairsWithMaxLength(
            @Parameter(description = "Maximum allowed combined length of quote pairs",
                    required = true,
                    example = "100")
            @PathVariable int maxLength) {

        Long pairsCount = quotePairService.countPairsWithMaxLength(maxLength);
        PairsCountDTO pairsCountDTO = new PairsCountDTO();
        pairsCountDTO.setCount(pairsCount);
        pairsCountDTO.setMaxLength(maxLength);
        return ResponseEntity.ok(pairsCountDTO);
    }
}