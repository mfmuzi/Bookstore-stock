package com.dio.bookstore.controller;

import com.dio.bookstore.builder.BookDTOBuilder;
import com.dio.bookstore.dto.BookDTO;
import com.dio.bookstore.dto.QuantityDTO;
import com.dio.bookstore.exceptions.BookNotFoundException;
import com.dio.bookstore.exceptions.BookStockExceededException;
import com.dio.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.dio.bookstore.utils.JsonConvertionUtils.asJsonString;


@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private static final String BOOK_API_URL_PATH = "/api/books";
    private static final long VALID_BOOK_ID = 1L;
    private static final long INVALID_BOOK_ID = 2l;
    private static final String BOOK_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BOOK_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABookIsCreated() throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        Mockito.when(bookService.createBook(bookDTO)).thenReturn(bookDTO);

        mockMvc.perform(post(BOOK_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(bookDTO.getTitle())))
                .andExpect(jsonPath("$.author", is(bookDTO.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookDTO.getGenre().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiresFieldThenAnErrorIsReturned () throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        bookDTO.setAuthor(null);

        mockMvc.perform(post(BOOK_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidTitleThenOkStatusIsReturned() throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        when(bookService.findByTitle(bookDTO.getTitle())).thenReturn(bookDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(BOOK_API_URL_PATH +"/" + bookDTO.getTitle())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(bookDTO.getTitle())))
                .andExpect(jsonPath("$.author", is(bookDTO.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookDTO.getGenre().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredTitleThenNotFoundStatusIsReturned() throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        when(bookService.findByTitle(bookDTO.getTitle())).thenThrow(BookNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BOOK_API_URL_PATH +"/" + bookDTO.getTitle())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void whenGETListOfBookIsCalledThenOkStatusIsReturned() throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        when(bookService.listAll()).thenReturn(Collections.singletonList(bookDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(BOOK_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is(bookDTO.getTitle())))
                .andExpect(jsonPath("$[0].author", is(bookDTO.getAuthor())))
                .andExpect(jsonPath("$[0].genre", is(bookDTO.getGenre().toString())));
    }

    @Test
    void whenGETListWithoutBookIsCalledThenOkStatusIsReturned() throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        when(bookService.listAll()).thenReturn(Collections.singletonList(bookDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(BOOK_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        doNothing().when(bookService).deleteById(bookDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(BOOK_API_URL_PATH +"/" + bookDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {

        doThrow(BookNotFoundException.class).when(bookService).deleteById(INVALID_BOOK_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(BOOK_API_URL_PATH +"/" + INVALID_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOkStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(2)
                .build();

        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        bookDTO.setQuantity(bookDTO.getQuantity() + quantityDTO.getQuantity());

        when(bookService.increment(VALID_BOOK_ID, quantityDTO.getQuantity())).thenReturn(bookDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(BOOK_API_URL_PATH + "/" + VALID_BOOK_ID + BOOK_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(bookDTO.getTitle())))
                .andExpect(jsonPath("$.author", is(bookDTO.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookDTO.getGenre().toString())))
                .andExpect(jsonPath("$.quantity", is(bookDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreaterThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        bookDTO.setQuantity(bookDTO.getQuantity() + quantityDTO.getQuantity());

        when(bookService.increment(VALID_BOOK_ID, quantityDTO.getQuantity())).thenThrow(BookStockExceededException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(BOOK_API_URL_PATH + "/" + VALID_BOOK_ID + BOOK_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBookIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(2)
                .build();

        when(bookService.increment(INVALID_BOOK_ID, quantityDTO.getQuantity())).thenThrow(BookNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(BOOK_API_URL_PATH + "/" + INVALID_BOOK_ID + BOOK_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOkStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        bookDTO.setQuantity(bookDTO.getQuantity() + quantityDTO.getQuantity());

        when(bookService.decrement(VALID_BOOK_ID, quantityDTO.getQuantity())).thenReturn(bookDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(BOOK_API_URL_PATH + "/" + VALID_BOOK_ID + BOOK_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(bookDTO.getTitle())))
                .andExpect(jsonPath("$.author", is(bookDTO.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookDTO.getGenre().toString())))
                .andExpect(jsonPath("$.quantity", is(bookDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(6)
                .build();

        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        bookDTO.setQuantity(bookDTO.getQuantity() + quantityDTO.getQuantity());

        when(bookService.decrement(VALID_BOOK_ID, quantityDTO.getQuantity())).thenThrow(BookStockExceededException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(BOOK_API_URL_PATH + "/" + VALID_BOOK_ID + BOOK_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBookIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(bookService.decrement(INVALID_BOOK_ID, quantityDTO.getQuantity())).thenThrow(BookNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(BOOK_API_URL_PATH + "/" + INVALID_BOOK_ID + BOOK_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

}
