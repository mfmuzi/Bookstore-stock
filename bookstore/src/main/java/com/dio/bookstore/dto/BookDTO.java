package com.dio.bookstore.dto;

import com.dio.bookstore.enums.BookType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String title;

    @NotNull
    @Size(min = 1, max = 200)
    private String author;

    @NotNull
    @Max(10)
    private Integer max;

    @NotNull
    @Max(10)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BookType genre;
}
