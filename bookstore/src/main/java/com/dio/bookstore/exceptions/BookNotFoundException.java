package com.dio.bookstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends Exception{

    public BookNotFoundException(String bookTitle) {
        super(String.format("Book with title %s not found in the system.", bookTitle));
    }

    public BookNotFoundException(Long id) {
        super(String.format("Book with id %s not found in the system.", id));
    }
}
