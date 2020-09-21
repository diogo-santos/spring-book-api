package com.book.service;

import com.book.service.repo.Book;
import com.book.service.repo.BookRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(value = {"/schema.sql", "/data.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class BookControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private BookRepository repository;

	@Test
	public void whenPerformGetBooksWithoutPagination_ThenApplyDefaultPagination() throws Exception {
		//Given a pre existing set of books

		//When
		ResultActions getBooksResponse = mockMvc.perform(get("/books"));
		//Then
		getBooksResponse
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements", is(7)))
				.andExpect(jsonPath("$.books", hasSize(5)))
				.andExpect(jsonPath("$.books[0].id", is(3)))
				.andExpect(jsonPath("$.books[0].title", is("ReactJS by Example - Building Modern Web Applications")))
				.andExpect(jsonPath("$.books[0].category", is("Computers")))
				.andExpect(jsonPath("$.books[0].author", is("Vipul A M")))
				.andExpect(jsonPath("$.books[0].publishedDate", is("2016-04-21")))
				.andExpect(jsonPath("$.books[0].image", is("http://books.google.com/books/content?id=Ht3JDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")));
	}

	@Test
	public void whenPerformGetBooksWithPagination_ThenListBooksIsReturned() throws Exception {
		//Given a pre existing set of books

		//When
		ResultActions getBooksResponse = mockMvc.perform(get("/books?pageNumber=2&pageSize=4&sortBy=author"));
		//Then
		getBooksResponse
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements", is(7)))
				.andExpect(jsonPath("$.books", hasSize(3)))
				.andExpect(jsonPath("$.books[0].id", is(4)))
				.andExpect(jsonPath("$.books[0].title", is("JavaScript React")))
				.andExpect(jsonPath("$.books[0].category", is("Computers")))
				.andExpect(jsonPath("$.books[0].author", is("Victor Deras")))
				.andExpect(jsonPath("$.books[0].publishedDate", is("2019-01-01")))
				.andExpect(jsonPath("$.books[0].image", is(emptyOrNullString())));
	}

	@Test
	public void whenPerformGetBookById_ThenBookIsReturned() throws Exception {
		//Given a pre existing set of books

		//When
		ResultActions getBooksResponse = mockMvc.perform(get("/books/6"));
		//Then
		getBooksResponse
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(6)))
				.andExpect(jsonPath("$.title", is("ReactJS Fundamentals")))
				.andExpect(jsonPath("$.category", is("Computers")))
				.andExpect(jsonPath("$.author", is("Charles David Crawford")))
				.andExpect(jsonPath("$.publishedDate", is("2018-01-01")))
				.andExpect(jsonPath("$.image", is(emptyOrNullString())));
	}

	@Test
	public void whenPerformDelete_ThenBookIsDeleted() throws Exception {
		//Given a existing book
		mockMvc.perform(get("/books/7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(7)));
		//When
		mockMvc.perform(delete("/books/7")).andExpect(status().isOk());
		//Then
		mockMvc.perform(get("/books/7"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));
	}

	@Test
	public void givenBookWhenPerformPostThenBookIsCreated() throws Exception {
		//Given book
		String bookJson =
				"{" +
				"\"title\": \"Mock title\"," +
				"\"author\":\"Mock author\"," +
				"\"category\": \"Mock category\", " +
				"\"publishedDate\": \"2020-01-01\"" +
				"}";
		//When
		mockMvc.perform(post("/books")
				.contentType(APPLICATION_JSON)
				.content(bookJson))
				.andExpect(status().isCreated());
		//Then
		Iterable<Book> books = repository.findAll();
		assertThat(books).extracting(Book::getTitle).contains("Mock title");
		assertThat(books).extracting(Book::getAuthor).contains("Mock author");
		assertThat(books).extracting(Book::getCategory).contains("Mock category");
		assertThat(books).extracting(Book::getPublishedDate).contains(LocalDate.of(2020, 1, 1));
	}

	@Test
	public void givenBookWithPublishYearOnlyWhenPerformPostThenBookIsCreated() throws Exception {
		//Given book
		String bookJson =
				"{" +
						"\"title\": \"Mock title\"," +
						"\"author\":\"Mock author\"," +
						"\"category\": \"Mock category\", " +
						"\"publishedDate\": \"1999\"" +
						"}";
		//When
		mockMvc.perform(post("/books")
				.contentType(APPLICATION_JSON)
				.content(bookJson))
				.andExpect(status().isCreated());
		//Then
		Iterable<Book> books = repository.findAll();
		assertThat(books).extracting(Book::getTitle).contains("Mock title");
		assertThat(books).extracting(Book::getAuthor).contains("Mock author");
		assertThat(books).extracting(Book::getCategory).contains("Mock category");
		assertThat(books).extracting(Book::getPublishedDate).contains(LocalDate.of(1999, 1, 1));
	}

	@Test
	public void givenBookWithoutTitleWhenPerformPostThenFieldErrorIsReturned() throws Exception {
		//Given book without title
		String bookJson = "{" +
				"\"title\": \"\"," +
				"\"author\":\"Mock author\"," +
				"\"category\": \"Mock category\", " +
				"\"publishedDate\": \"2020-01-01\"" +
				"}";
		//When
		ResultActions postBooksResponse = mockMvc.perform(post("/books")
				.contentType(APPLICATION_JSON)
				.content(bookJson));
		//Then
		postBooksResponse
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.title", is("must not be blank")));
	}

	@Test
	public void givenBookWithInvalidDateFormatWhenPerformPostThenErrorIsReturned() throws Exception {
		//Given book without title
		String bookJson = "{" +
				"\"title\": \"Mock title\"," +
				"\"author\":\"Mock author\"," +
				"\"category\": \"Mock category\", " +
				"\"publishedDate\": \"01/01/2000\"" +
				"}";
		//When
		ResultActions postBooksResponse = mockMvc.perform(post("/books")
				.contentType(APPLICATION_JSON)
				.content(bookJson));
		//Then
		postBooksResponse
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", is("Invalid date 01/01/2000. It should be a year (yyyy) or date (yyyy-MM-dd) format")));
	}

	@Test
	public void givenBookWithInvalidYearWhenPerformPostThenErrorIsReturned() throws Exception {
		//Given book without title
		String bookJson = "{" +
				"\"title\": \"Mock title\"," +
				"\"author\":\"Mock author\"," +
				"\"category\": \"Mock category\", " +
				"\"publishedDate\": \"12/1\"" +
				"}";
		//When
		ResultActions postBooksResponse = mockMvc.perform(post("/books")
				.contentType(APPLICATION_JSON)
				.content(bookJson));
		//Then
		postBooksResponse
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", is("Invalid date 12/1. It should be a year (yyyy) or date (yyyy-MM-dd) format")));
	}
}