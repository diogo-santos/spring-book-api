package com.book.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class BookControllerExceptionTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private BookService bookService;

	@Test
	public void whenPerformDeleteOnError_ThenDefaultErrorMessageIsReturn() throws Exception {
		//Given
		doThrow(new IllegalStateException("Error occurred"))
				.when(bookService)
				.delete(anyLong());
		//When
		ResultActions deleteBookResponse = mockMvc.perform(delete("/books/7"));
		//Then
		deleteBookResponse
				.andExpect(status().is5xxServerError())
				.andExpect(jsonPath("$.message", is("Error while trying to access the resource")));
	}
}