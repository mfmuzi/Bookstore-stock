package com.dio.bookstore.builder;

import com.dio.bookstore.dto.BookDTO;
import com.dio.bookstore.enums.BookType;
import lombok.Builder;

@Builder
public class BookDTOBuilder {
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String title = "O Senhor dos Aneis";

    @Builder.Default
    private String author = "J. R. R. Tolkien";

    @Builder.Default
    private int max = 10;

    @Builder.Default
    private int quantity = 2;

    @Builder.Default
    private BookType genre = BookType.Fantasy;

    public BookDTO toBookDTO() {
        return new BookDTO(id,
                title,
                author,
                max,
                quantity,
                genre);
    }
}
