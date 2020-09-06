package com.book.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
				.andExpect(jsonPath("$.books[0].publicationDate", is("2016-04-21")))
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
				.andExpect(jsonPath("$.books[0].publicationDate", is("2019-01-01")))
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
				.andExpect(jsonPath("$.publicationDate", is("2018-01-01")))
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
}