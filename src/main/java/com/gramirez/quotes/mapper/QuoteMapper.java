package com.gramirez.quotes.mapper;

import com.gramirez.quotes.dto.QuoteDTO;
import com.gramirez.quotes.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuoteMapper {

    private final AuthorMapper authorMapper;

    @Autowired
    public QuoteMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public QuoteDTO toDto (Quote quote) {
        if (quote == null) return null;

        QuoteDTO quoteDTO = new QuoteDTO();
        quoteDTO.setId(quote.getId());
        quoteDTO.setText(quote.getText());
        quoteDTO.setAuthor(authorMapper.toDTO(quote.getAuthor()));

        return quoteDTO;
    }
}
