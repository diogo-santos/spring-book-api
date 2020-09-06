package com.book.service;

import com.book.service.domain.Book;
import com.book.service.domain.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    Page<BookDto> findAllBy(Pageable paging);
    BookDto findBookById(Long id);
}