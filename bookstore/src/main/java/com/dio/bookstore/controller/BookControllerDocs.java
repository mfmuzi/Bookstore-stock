package com.dio.bookstore.controller;

import com.dio.bookstore.dto.BookDTO;
import com.dio.bookstore.exceptions.BookAlreadyRegisteredException;
import com.dio.bookstore.exceptions.BookNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages book stock")
public interface BookControllerDocs {

    @ApiOperation(value = "Book creation operation")

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success book creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    BookDTO createBook(BookDTO bookDTO) throws BookAlreadyRegisteredException;

    @ApiOperation(value = "Returns book found by a given title")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success book found in the system"),
            @ApiResponse(code = 404, message = "Book with given title not found.")
    })
    BookDTO findByTitle(@PathVariable String title) throws BookNotFoundException;

    @ApiOperation(value = "Returns a list of all books registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all books registered in the system"),
    })
    List<BookDTO> listBooks();

    @ApiOperation(value = "Delete a book found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success book deleted in the system"),
            @ApiResponse(code = 404, message = "Book with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws BookNotFoundException;
}
