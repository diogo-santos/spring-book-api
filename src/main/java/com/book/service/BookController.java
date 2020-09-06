package com.book.service;

import com.book.service.domain.BookDto;
import com.book.service.domain.PageBookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/")
public class BookController {
    private final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<PageBookDto> getAllBooks(@RequestParam(defaultValue = "1") final Integer pageNumber,
                                                   @RequestParam(defaultValue = "5") final Integer pageSize,
                                                   @RequestParam(defaultValue = "publicationDate") final String sortBy) {
        logger.info("In getAllBooks with pageNumber {} pageSize {} sortBy {}", pageNumber, pageSize, sortBy);
        PageBookDto page = bookService.getAllBooks(pageNumber, pageSize, sortBy);
        logger.info("Out getAllBooks with pageNumber {} pageSize {} sortBy {}", pageNumber, pageSize, sortBy);
        return new ResponseEntity<>(page, OK);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable final Long id) {
        logger.info("In getBookById with id {}", id);
        BookDto book = bookService.getBookById(id);
        logger.info("Out getBookById with id {} book {}", id, book);
        return new ResponseEntity<>(book, OK);
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(OK)
    public void deleteBook(@PathVariable final Long id) {
        logger.info("In deleteBook with id {}", id);
        this.bookService.delete(id);
        logger.info("Out deleteBook with id {}", id);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Error while trying to access the resource");
        return body;
    }
}