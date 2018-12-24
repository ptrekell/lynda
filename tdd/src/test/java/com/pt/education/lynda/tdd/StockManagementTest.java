package com.pt.education.lynda.tdd;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class StockManagementTest {


//    https://stackoverflow.com/questions/23086932/cannot-instantiate-injectmocks-field-named-exception-with-java-class
    @Mock
    ExternalISBNDataService databaseService;

    @Mock
    ExternalISBNDataService webService;

    StockManager stockManager;

    @Before
    public void setUp() {
        initMocks(this);
        stockManager = new StockManager();

        stockManager.setWebService(webService);
        stockManager.setDatabaseService(databaseService);
    }

    @Test
    public void testCanGetACorrectLocatorCode() {

        ExternalISBNDataService testWebService = isbn -> new Book(isbn, "Of Mice And Men", "J. Steinbeck");
        ExternalISBNDataService testDatabaseService = isbn -> null;

        stockManager.setWebService(testWebService);
        stockManager.setDatabaseService(testDatabaseService);

        String isbn = "0140177396";
        String locatorCode = stockManager.getLocatorCode(isbn);
        assertEquals("7396J4", locatorCode);
    }

    @Test
    public void databaseIsUsedIfDataIsPresent() {

        when(databaseService.lookup("0140177396")).thenReturn(new Book("0140177396","abc","abc"));

        String isbn = "0140177396";
        String locatorCode = stockManager.getLocatorCode(isbn);

        verify(databaseService).lookup("0140177396");
        verify(webService, never()).lookup(anyString());

    }

    @Test
    public void webserviceIsUsedIfDataIsNotPresentInDatabase() throws Exception {

        when(databaseService.lookup("0140177396")).thenReturn(null);
        when(webService.lookup("0140177396")).thenReturn(new Book("0140177396","abc","abc"));


        Logger logger = mock(Logger.class);
//        logger =  LoggerFactory.getLogger(StockManagementTest.class);
        setFinalStatic(StockManager.class.getDeclaredField("log"), logger);


        String isbn = "0140177396";
        String locatorCode = stockManager.getLocatorCode(isbn);

        verify(logger).info("Getting locator code for ISBN: " + isbn);
        verify(logger).info("Returned locator for ISBN: " + isbn);



        verify(databaseService).lookup(isbn);
        verify(webService,times(1)).lookup(isbn);
    }


    //https://stackoverflow.com/questions/30703149/mock-private-static-final-field-using-mockito-or-jmockit
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    //some more solutions
    //https://stackoverflow.com/questions/8948916/mocking-logger-and-loggerfactory-with-powermock-and-mockito

}
