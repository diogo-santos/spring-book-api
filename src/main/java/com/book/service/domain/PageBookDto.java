package com.book.service.domain;

import com.book.service.repo.BookView;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageBookDto {
    private Long totalElements;
    private List<BookView> books;

    public static PageBookDto from(Page<BookView> page) {
        return builder()
                .totalElements(page.getTotalElements())
                .books(page.getContent())
                .build();
    }
}
