package com.book.service.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateBookDto {
    @NotBlank
    private String title;
    @NotBlank
    private String category;
    @NotBlank
    private String author;
    private String publishedDate;
    private String image;
}
