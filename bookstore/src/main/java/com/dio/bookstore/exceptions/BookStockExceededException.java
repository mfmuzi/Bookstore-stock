package com.dio.bookstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookStockExceededException extends Exception{
    public BookStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Books with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
