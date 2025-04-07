package com.gramirez.quotes.dto;

import lombok.Data;

@Data
public class CreateQuoteDTO {
    private String text;
    private String author;
}
