package com.gramirez.quotes.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("import")
public class QuoteImporterServiceRunner implements CommandLineRunner {
    private final QuoteImporterService quoteImporterService;
    private static final Logger logger = LoggerFactory.getLogger(QuoteImporterServiceRunner.class);

    @Value("${quotes.import.file.path}")
    private String quotesFilePath;

    public QuoteImporterServiceRunner(QuoteImporterService quoteImporterService) {
        this.quoteImporterService = quoteImporterService;
    }

    @Override
    public void run(String... args) {
        logger.info("Importing quotes at startup");
        int count = quoteImporterService.importQuotesFromFile(quotesFilePath);
        logger.info("Importation completed. {} quotes imported", count);

    }
}
