package com.book.service;

import com.book.service.domain.Book;
import com.book.service.domain.BookDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class BookRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private BookRepository repository;

	@Test
	public void testFindAllBooksWithPagingAndSortByPublicationDate() {
		// Given a pre existing set of books

		// When
		Pageable paging = PageRequest.of(0, 3, Sort.by("publicationDate"));
		Page<BookDto> page = repository.findAllBy(paging);

		// Then
		assertThat(page).isNotNull();
		assertThat(page.hasContent()).isTrue();
		assertThat(page.getContent()).hasSize(3);

		assertThat(page.getContent()).extracting(bookDto -> bookDto.getTitle().toLowerCase()).contains("reactjs");
		assertThat(page.getContent()).extracting(BookDto::getAuthor).contains("Vipul A M", "Todd Abel", "Charles David Crawford");
		assertThat(page.getContent()).extracting(BookDto::getCategory).containsOnly("Computers");
		assertThat(page.getContent()).extracting(BookDto::getImage).containsOnly(
				"http://books.google.com/books/content?id=Ht3JDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
				"http://books.google.com/books/content?id=O7nAjwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
				"");
		assertThat(page.getContent()).first().extracting(BookDto::getPublicationDate).isEqualTo(LocalDate.of(2016, 4, 21));
		assertThat(page.getContent()).last().extracting(BookDto::getPublicationDate).isEqualTo(LocalDate.of(2018, 1, 1));

		assertThat(page.getTotalElements()).isEqualTo(7);
		assertThat(page.getTotalPages()).isEqualTo(3);
		assertThat(page.getNumberOfElements()).isEqualTo(3);
	}

	@Test
	public void testFindBookById() {
		// Given
		Book bookToSave = new Book();
		bookToSave.setTitle("Title");
		bookToSave.setAuthor("Author");
		bookToSave.setCategory("Category");
		bookToSave.setImage("ImageUrl");
		bookToSave.setPublicationDate(LocalDate.of(2020, 1, 1));

		Book book = entityManager.persist(bookToSave);

		// When
		BookDto bookDto = repository.findBookById(book.getId());

		// Then
		assertThat(bookDto).isNotNull();
		assertThat(bookDto.getId()).isEqualTo(book.getId());
		assertThat(bookDto.getTitle()).isEqualTo(book.getTitle());
		assertThat(bookDto.getAuthor()).isEqualTo(book.getAuthor());
		assertThat(bookDto.getCategory()).isEqualTo(book.getCategory());
		assertThat(bookDto.getPublicationDate()).isEqualTo(book.getPublicationDate());
		assertThat(bookDto.getImage()).isEqualTo(book.getImage());
	}
}