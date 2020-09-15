package com.book.service;

import com.book.service.repo.BookView;
import com.book.service.domain.PageBookDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class BookServiceTest {
	@Autowired
	private BookService bookService;

	/**
	 * Given - Valid pagination and sort params
	 * When - Get all books
	 * Then - It should search books with pagination
	 */
	@Test
	public void testGetAllBooksWithPagingAndSortByPublishedDate() {
		// Given
		int pageNumber = 1;
		int pageSize = 3;
		String sortBy = "publishedDate";

		// When
		PageBookDto page = bookService.getAllBooks(pageNumber, pageSize, sortBy);

		// Then
		assertThat(page).isNotNull();
		assertThat(page.getBooks()).hasSize(3);

		assertThat(page.getBooks()).extracting(bookDto -> bookDto.getTitle().toLowerCase()).contains("reactjs");
		assertThat(page.getBooks()).extracting(BookView::getAuthor).contains("Vipul A M", "Todd Abel", "Charles David Crawford");
		assertThat(page.getBooks()).extracting(BookView::getCategory).containsOnly("Computers");
		assertThat(page.getBooks()).first().extracting(BookView::getPublishedDate).isEqualTo(LocalDate.of(2016, 4, 21));
		assertThat(page.getBooks()).last().extracting(BookView::getPublishedDate).isEqualTo(LocalDate.of(2018, 1, 1));

		assertThat(page.getTotalElements()).isEqualTo(7);
	}

	/**
	 * Given - Invalid values
	 * When - Get all books
	 * Then - It should search books using default values
	 */
	@Test
	public void testGetAllBooksWithInvalidPagingAndSortParams() {
		// Given
		int pageNumber = -1;
		int pageSize = -1;
		String sortBy = "invalidField";

		// When
		PageBookDto page = bookService.getAllBooks(pageNumber, pageSize, sortBy);

		// Then
		assertThat(page).isNotNull();
		assertThat(page.getBooks()).hasSize(5);

		assertThat(page.getBooks()).extracting(bookDto -> bookDto.getTitle().toLowerCase()).contains("reactjs");
		assertThat(page.getBooks()).extracting(BookView::getAuthor).contains("Vipul A M", "Todd Abel", "Charles David Crawford");
		assertThat(page.getBooks()).extracting(BookView::getCategory).containsOnly("Computers");
		assertThat(page.getBooks()).extracting(BookView::getImage).contains("", null,
				"http://books.google.com/books/content?id=O7nAjwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"
		);
		assertThat(page.getBooks()).first().extracting(BookView::getPublishedDate).isEqualTo(LocalDate.of(2016, 4, 21));
		assertThat(page.getBooks()).last().extracting(BookView::getPublishedDate).isEqualTo(LocalDate.of(2019, 1, 1));

		assertThat(page.getTotalElements()).isEqualTo(7);
	}

	/**
	 * Given - Valid Id
	 * When - Get book by id
	 * Then - It should return book
	 */
	@Test
	public void testGetBookById() {
		// Given
		Long id = 6L;

		// When
		BookView bookView = bookService.getBookById(id);

		// Then
		assertThat(bookView).isNotNull();
		assertThat(bookView.getId()).isEqualTo(id);
		assertThat(bookView.getTitle()).isEqualTo("ReactJS Fundamentals");
		assertThat(bookView.getAuthor()).isEqualTo("Charles David Crawford");
		assertThat(bookView.getCategory()).isEqualTo("Computers");
		assertThat(bookView.getPublishedDate()).isEqualTo(LocalDate.of(2018, 1, 1));
	}
}