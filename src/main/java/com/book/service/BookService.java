package com.book.service;

import com.book.service.domain.BookDto;
import com.book.service.domain.BookSortEnum;
import com.book.service.domain.PageBookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.book.service.domain.BookSortEnum.PUBLICATION_DATE;

@Service
public class BookService {
    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public PageBookDto getAllBooks(final int pageNumber, final int pageSize, final String sortBy) {
        int page = pageNumber < 1 ? 0 : pageNumber - 1;
        int size = pageSize < 1 ? 5 : pageSize;
        String field = BookSortEnum.contains(sortBy)? sortBy : PUBLICATION_DATE.getField();

        Pageable pageable = PageRequest.of(page, size, Sort.by(field));
        Page<BookDto> pageBooks = bookRepo.findAllBy(pageable);

        return PageBookDto.from(pageBooks);
    }

    public BookDto getBookById(final Long id) {
        return bookRepo.findBookById(id);
    }

    public void delete(final Long id) {
        bookRepo.deleteById(id);
    }
}
