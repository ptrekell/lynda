package com.pt.education.lynda.tdd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StockManager {

    private static final Logger log =  LoggerFactory.getLogger(StockManager.class);

    private ExternalISBNDataService webService;
    private ExternalISBNDataService databaseService;

    public void setWebService(ExternalISBNDataService service) {
        this.webService = service;
    }


    public void setDatabaseService(ExternalISBNDataService databaseService) {
        this.databaseService = databaseService;
    }


    public String getLocatorCode(String isbn) {
        log.info("Getting locator code for ISBN: " + isbn);
        Book book = databaseService.lookup(isbn);
        if (book == null) book = webService.lookup(isbn);
        StringBuilder locator = new StringBuilder();
        locator.append(isbn.substring(isbn.length() - 4));
        locator.append(book.getAuthor().substring(0, 1));
        locator.append(book.getTitle().split(" ").length);

        log.info("Returned locator for ISBN: " + isbn);
        return locator.toString();
    }

}