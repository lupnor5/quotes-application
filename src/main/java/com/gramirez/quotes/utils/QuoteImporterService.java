package com.gramirez.quotes.utils;

import com.gramirez.quotes.dto.CreateQuoteDTO;
import com.gramirez.quotes.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuoteImporterService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteImporterService.class);
    private static final Pattern QUOTE_PATTERN = Pattern.compile("\\{\"Id\":\\d+,\"Author\":\"([^\"]*)\",\"Text\":\"([^\"]*)\"\\}");
    private static final int CHUNK_SIZE = 10000;

    private final QuoteService quoteService;

    @Autowired
    public QuoteImporterService(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    public int importQuotesFromFile(String filePath) {
        AtomicInteger successCount = new AtomicInteger(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }

            String jsonArray = fileContent.toString();
            Matcher matcher = QUOTE_PATTERN.matcher(jsonArray);

            List<QuoteData> quoteBuffer = new ArrayList<>(CHUNK_SIZE);

            while (matcher.find()) {
                String author = matcher.group(1);
                String text = matcher.group(2);

                if (text == null || text.isEmpty() || author == null || author.isEmpty()) {
                    logger.warn("Empty text or author in quote");
                    continue;
                }

                quoteBuffer.add(new QuoteData(author, text));

                if (quoteBuffer.size() >= CHUNK_SIZE) {
                    int processed = processQuoteBatch(quoteBuffer);
                    successCount.addAndGet(processed);
                    quoteBuffer.clear();

                    logger.info("Processed chunk of {} quotes. Total so far: {}",
                            CHUNK_SIZE, successCount.get());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (!quoteBuffer.isEmpty()) {
                int processed = processQuoteBatch(quoteBuffer);
                successCount.addAndGet(processed);
                logger.info("Processed final chunk of {} quotes", quoteBuffer.size());
            }

            logger.info("Import completed successfully. Total quotes imported: {}", successCount.get());
            return successCount.get();

        } catch (IOException e) {
            logger.error("Failed to read file: {}", filePath, e);
            return 0;
        }
    }

    @Transactional
    public int processQuoteBatch(List<QuoteData> quotes) {
        int successCount = 0;

        for (QuoteData quote : quotes) {
            try {
                CreateQuoteDTO quoteDTO = new CreateQuoteDTO();
                quoteDTO.setText(quote.text);
                quoteDTO.setAuthor(quote.author);

                quoteService.create(quoteDTO);
                successCount++;

            } catch (Exception e) {
                logger.error("Error processing quote. Author: {}, Text: {}",
                        quote.author, quote.text, e);
            }
        }

        return successCount;
    }

    private static class QuoteData {
        private final String author;
        private final String text;

        public QuoteData(String author, String text) {
            this.author = author;
            this.text = text;
        }
    }
}