package com.dio.bookstore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookType {

    Action("Action"),
    Adventure("Adventure"),
    Detective("Detective"),
    Suspense("Suspense"),
    Fantasy("Fantasy"),
    Horror("Horror"),
    Poetry("Poetry"),
    Romance("Romance"),
    SciFi("Sci-Fi"),
    History("History"),
    Literary("Literary");

    private final String description;
}
