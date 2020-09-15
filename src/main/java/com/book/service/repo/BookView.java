package com.book.service.repo;

import java.time.LocalDate;

public interface BookView {
    Long getId();
    String getTitle();
    String getCategory();
    String getAuthor();
    LocalDate getPublishedDate();
    String getImage();
}
