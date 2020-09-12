package com.book.service.domain;

import java.util.Arrays;

public enum BookSortEnum {
    ID("id"),
    TITLE("title"),
    AUTHOR("author"),
    CATEGORY("category"),
    PUBLISHED_DATE("publishedDate");

    private final String field;

    BookSortEnum(String field) {
        this.field = field;
    }

    public String getField() {
        return this.field;
    }
    public static boolean contains(String field) {
        return Arrays.stream(BookSortEnum.values())
                .anyMatch(sort -> sort.getField().equals(field));
    }
}
