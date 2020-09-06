package com.book.service.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageBookDto {
    private Long totalElements;
    private List<BookDto> books;

    public static PageBookDto from(Page<BookDto> page) {
        return builder()
                .totalElements(page.getTotalElements())
                .books(page.getContent())
                .build();
    }
}
