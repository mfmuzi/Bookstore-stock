package com.dio.bookstore.controller;

import com.dio.bookstore.dto.BookDTO;
import com.dio.bookstore.dto.QuantityDTO;
import com.dio.bookstore.exceptions.BookAlreadyRegisteredException;
import com.dio.bookstore.exceptions.BookNotFoundException;
import com.dio.bookstore.exceptions.BookStockExceededException;
import com.dio.bookstore.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BookController implements BookControllerDocs {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody @Valid BookDTO bookDTO) throws BookAlreadyRegisteredException {
        return bookService.createBook(bookDTO);
    }

    @GetMapping("/{title}")
    public BookDTO findByTitle(@PathVariable String title) throws BookNotFoundException {
        return bookService.findByTitle(title);
    }

    @GetMapping
    public List<BookDTO> listBooks() {
        return bookService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws BookNotFoundException {
        bookService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public BookDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BookNotFoundException, BookStockExceededException {
        return bookService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public BookDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BookNotFoundException, BookStockExceededException {
        return bookService.decrement(id, quantityDTO.getQuantity());
    }
}
