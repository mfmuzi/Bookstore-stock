package com.dio.bookstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookAlreadyRegisteredException extends Exception{

    public BookAlreadyRegisteredException(String bookTitle) {
        super(String.format("Book with title %s already registered in the system.", bookTitle));
    }
}
