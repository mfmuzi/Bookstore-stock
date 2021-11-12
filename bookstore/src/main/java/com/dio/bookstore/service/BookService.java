package com.dio.bookstore.service;

import com.dio.bookstore.dto.BookDTO;
import com.dio.bookstore.entity.Book;
import com.dio.bookstore.exceptions.BookAlreadyRegisteredException;
import com.dio.bookstore.exceptions.BookNotFoundException;
import com.dio.bookstore.exceptions.BookStockExceededException;
import com.dio.bookstore.mapper.BookMapper;
import com.dio.bookstore.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper = BookMapper.INSTANCE;

    public BookDTO createBook(BookDTO bookDTO) throws BookAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(bookDTO.getTitle());
        Book book = bookMapper.toModel(bookDTO);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }

    public BookDTO findByTitle(String title) throws BookNotFoundException {
        Book foundBook = bookRepository.findByTitle(title)
                .orElseThrow(() -> new BookNotFoundException(title));
        return bookMapper.toDTO(foundBook);
    }

    public List<BookDTO> listAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BookNotFoundException {
        verifyIfExists(id);
        bookRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String title) throws BookAlreadyRegisteredException {
        Optional<Book> optSavedBook = bookRepository.findByTitle(title);
        if (optSavedBook.isPresent()) {
            throw new BookAlreadyRegisteredException(title);
        }
    }

    private Book verifyIfExists(Long id) throws BookNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public BookDTO increment(Long id, int quantityToIncrement) throws BookNotFoundException, BookStockExceededException {
        Book bookToIncrementStock = verifyIfExists(id);

        if(quantityToIncrement + bookToIncrementStock.getQuantity() <= bookToIncrementStock.getMax()){

            bookToIncrementStock.setQuantity(bookToIncrementStock.getQuantity() + quantityToIncrement);
            Book incrementedBookStock = bookRepository.save(bookToIncrementStock);
            return bookMapper.toDTO(incrementedBookStock);
        }
        throw new BookStockExceededException(id, quantityToIncrement);

    }

    public BookDTO decrement(Long id, int quantityToDecrement) throws BookNotFoundException, BookStockExceededException {
        Book bookToDecrementStock = verifyIfExists(id);

        int bookStockAfterDecremented = bookToDecrementStock.getQuantity() - quantityToDecrement;

        if ( bookStockAfterDecremented >= 0) {
            bookToDecrementStock.setQuantity(bookStockAfterDecremented);
            Book decrementedBookStock = bookRepository.save(bookToDecrementStock);
            return bookMapper.toDTO(decrementedBookStock);
        }
        throw new BookStockExceededException(id, quantityToDecrement);
    }
}
