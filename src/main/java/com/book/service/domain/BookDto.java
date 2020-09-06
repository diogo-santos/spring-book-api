package com.book.service.domain;

import java.time.LocalDate;

public interface BookDto {
    Long getId();
    String getTitle();
    String getCategory();
    String getAuthor();
    LocalDate getPublicationDate();
    String getImage();
}
