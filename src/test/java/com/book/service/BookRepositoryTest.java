package com.book.service;

import com.book.service.repo.Book;
import com.book.service.repo.BookView;
import com.book.service.repo.BookRepository;
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
	public void testFindAllBooksWithPagingAndSortByPublishedDate() {
		// Given a pre existing set of books

		// When
		Pageable paging = PageRequest.of(0, 3, Sort.by("publishedDate"));
		Page<BookView> page = repository.findAllBy(paging);

		// Then
		assertThat(page).isNotNull();
		assertThat(page.hasContent()).isTrue();
		assertThat(page.getContent()).hasSize(3);

		assertThat(page.getContent()).extracting(bookDto -> bookDto.getTitle().toLowerCase()).contains("reactjs");
		assertThat(page.getContent()).extracting(BookView::getAuthor).contains("Vipul A M", "Todd Abel", "Charles David Crawford");
		assertThat(page.getContent()).extracting(BookView::getCategory).containsOnly("Computers");
		assertThat(page.getContent()).extracting(BookView::getImage).containsOnly(
				"http://books.google.com/books/content?id=Ht3JDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
				"http://books.google.com/books/content?id=O7nAjwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
				"");
		assertThat(page.getContent()).first().extracting(BookView::getPublishedDate).isEqualTo(LocalDate.of(2016, 4, 21));
		assertThat(page.getContent()).last().extracting(BookView::getPublishedDate).isEqualTo(LocalDate.of(2018, 1, 1));

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
		bookToSave.setPublishedDate(LocalDate.of(2020, 1, 1));

		Book book = entityManager.persist(bookToSave);

		// When
		BookView bookView = repository.findBookById(book.getId());

		// Then
		assertThat(bookView).isNotNull();
		assertThat(bookView.getId()).isEqualTo(book.getId());
		assertThat(bookView.getTitle()).isEqualTo(book.getTitle());
		assertThat(bookView.getAuthor()).isEqualTo(book.getAuthor());
		assertThat(bookView.getCategory()).isEqualTo(book.getCategory());
		assertThat(bookView.getPublishedDate()).isEqualTo(book.getPublishedDate());
		assertThat(bookView.getImage()).isEqualTo(book.getImage());
	}
}