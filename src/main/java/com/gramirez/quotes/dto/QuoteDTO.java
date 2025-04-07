package com.gramirez.quotes.dto;

import lombok.Data;

@Data
public class QuoteDTO {
    private Long id;
    private String text;
    private AuthorDTO author;
}
