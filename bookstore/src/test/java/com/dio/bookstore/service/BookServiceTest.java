package com.dio.bookstore.service;

import com.dio.bookstore.builder.BookDTOBuilder;
import com.dio.bookstore.dto.BookDTO;
import com.dio.bookstore.entity.Book;
import com.dio.bookstore.exceptions.BookAlreadyRegisteredException;
import com.dio.bookstore.exceptions.BookNotFoundException;
import com.dio.bookstore.exceptions.BookStockExceededException;
import com.dio.bookstore.mapper.BookMapper;
import com.dio.bookstore.repository.BookRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final long INVALID_BOOK_ID = 1L;

    @Mock
    private BookRepository bookRepository;

    private BookMapper bookMapper = BookMapper.INSTANCE;

    @InjectMocks
    private BookService bookService;

    @Test
    void whenBookInformedThenItShouldBeCreated() throws BookAlreadyRegisteredException {
        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedSavedBook = bookMapper.toModel(expectedBookDTO);

        Mockito.when(bookRepository.findByTitle(expectedBookDTO.getTitle())).thenReturn(Optional.empty());
        Mockito.when(bookRepository.save(expectedSavedBook)).thenReturn(expectedSavedBook);

        BookDTO createdBookDTO = bookService.createBook(expectedBookDTO);

        MatcherAssert.assertThat(createdBookDTO.getId(), Matchers.is(Matchers.equalTo(expectedBookDTO.getId())));
        MatcherAssert.assertThat(createdBookDTO.getTitle(), Matchers.is(Matchers.equalTo(expectedBookDTO.getTitle())));
        MatcherAssert.assertThat(createdBookDTO.getQuantity(), Matchers.is(Matchers.equalTo(expectedBookDTO.getQuantity())));

    }

    @Test
    void whenAlreadyRegisteredBookInformedThenAnExceptionShouldBeThrown() {

        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book duplicatedBook = bookMapper.toModel(expectedBookDTO);

        Mockito.when(bookRepository.findByTitle(expectedBookDTO.getTitle())).thenReturn(Optional.of(duplicatedBook));

        assertThrows(BookAlreadyRegisteredException.class, () -> bookService.createBook(expectedBookDTO));
    }

    @Test
    void whenValidBooksTitleIsGivenThenReturnABook() throws BookNotFoundException {
        BookDTO expectedFoundBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedFoundBook = bookMapper.toModel(expectedFoundBookDTO);

        when(bookRepository.findByTitle(expectedFoundBook.getTitle())).thenReturn(Optional.of(expectedFoundBook));

        BookDTO foundBookDTO = bookService.findByTitle(expectedFoundBookDTO.getTitle());

        assertThat(foundBookDTO, is(equalTo(expectedFoundBookDTO)));
    }

    @Test
    void whenNoRegisteredBookTitleIsGivenThenThrowsAnException() {
        BookDTO expectedFoundBookDTO = BookDTOBuilder.builder().build().toBookDTO();

        when(bookRepository.findByTitle(expectedFoundBookDTO.getTitle())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findByTitle(expectedFoundBookDTO.getTitle()));

    }

    @Test
    void whenListOfBookIsCalledThenReturnAListOfBooks() {
        BookDTO expectedFoundBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedFoundBook = bookMapper.toModel(expectedFoundBookDTO);

        when(bookRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBook));

        List<BookDTO> foundListBooksDTO = bookService.listAll();

        assertThat(foundListBooksDTO, is(not(empty())));
        assertThat(foundListBooksDTO.get(0), is(equalTo(expectedFoundBookDTO)));

    }

    @Test
    void whenListOfBookIsCalledThenReturnAnEmptyListOfBooks() {

        when(bookRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<BookDTO> foundListBooksDTO = bookService.listAll();

        assertThat(foundListBooksDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithIdThenABookShouldBeDeleted() throws BookNotFoundException {
        BookDTO expectedDeletedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedDeletedBook = bookMapper.toModel(expectedDeletedBookDTO);

        when(bookRepository.findById(expectedDeletedBookDTO.getId())).thenReturn(Optional.of(expectedDeletedBook));
        doNothing().when(bookRepository).deleteById(expectedDeletedBookDTO.getId());

        bookService.deleteById(expectedDeletedBookDTO.getId());

        verify(bookRepository, times(1)).findById(expectedDeletedBookDTO.getId());
        verify(bookRepository, times(1)).deleteById(expectedDeletedBookDTO.getId());
    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteById(INVALID_BOOK_ID));
    }

    @Test
    void whenIncrementIsCalledThenIncrementBookStock() throws BookNotFoundException, BookStockExceededException {

        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBook = bookMapper.toModel(expectedBookDTO);

        when(bookRepository.findById(expectedBookDTO.getId())).thenReturn(Optional.of(expectedBook));
        when(bookRepository.save(expectedBook)).thenReturn(expectedBook);

        int quantityToIncrement = 2;
        int expectedQuantityAfterIncrement = expectedBookDTO.getQuantity() + quantityToIncrement;
        BookDTO incrementedBookDTO = bookService.increment(expectedBookDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBookDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedBookDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreaterThanMaxThenThrowException() {
        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBook = bookMapper.toModel(expectedBookDTO);

        when(bookRepository.findById(expectedBookDTO.getId())).thenReturn(Optional.of(expectedBook));

        int quantityToIncrement = 10;
        assertThrows(BookStockExceededException.class, () -> bookService.increment(expectedBookDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreaterThanMaxThenThrowException() {
        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBook = bookMapper.toModel(expectedBookDTO);

        when(bookRepository.findById(expectedBookDTO.getId())).thenReturn(Optional.of(expectedBook));

        int quantityToIncrement = 20;
        assertThrows(BookStockExceededException.class, () -> bookService.increment(expectedBookDTO.getId(), quantityToIncrement));
    }
    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 2;

        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.increment(INVALID_BOOK_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementBookStock() throws BookNotFoundException, BookStockExceededException {
        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBook = bookMapper.toModel(expectedBookDTO);

        when(bookRepository.findById(expectedBookDTO.getId())).thenReturn(Optional.of(expectedBook));
        when(bookRepository.save(expectedBook)).thenReturn(expectedBook);

        int quantityToDecrement = 1;
        int expectedQuantityAfterDecrement = expectedBookDTO.getQuantity() - quantityToDecrement;
        BookDTO incrementedBookDTO = bookService.decrement(expectedBookDTO.getId(), quantityToDecrement);

        assertThat(incrementedBookDTO.getQuantity(), is(equalTo(expectedQuantityAfterDecrement)));
        assertThat(expectedQuantityAfterDecrement, is(greaterThan(0)));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyBookStock() throws BookNotFoundException, BookStockExceededException {
        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBook = bookMapper.toModel(expectedBookDTO);

        when(bookRepository.findById(expectedBookDTO.getId())).thenReturn(Optional.of(expectedBook));
        when(bookRepository.save(expectedBook)).thenReturn(expectedBook);

        int quantityToDecrement = 2;
        int expectedQuantityAfterDecrement = expectedBookDTO.getQuantity() - quantityToDecrement;
        BookDTO incrementedBookDTO = bookService.decrement(expectedBookDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, is(equalTo(0)));
        assertThat(expectedQuantityAfterDecrement, is(equalTo(incrementedBookDTO.getQuantity())));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        BookDTO expectedBookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBook = bookMapper.toModel(expectedBookDTO);

        when(bookRepository.findById(expectedBookDTO.getId())).thenReturn(Optional.of(expectedBook));

        int quantityToDecrement = 10;
        assertThrows(BookStockExceededException.class, () -> bookService.decrement(expectedBookDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 5;

        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.decrement(INVALID_BOOK_ID, quantityToDecrement));
    }
}
