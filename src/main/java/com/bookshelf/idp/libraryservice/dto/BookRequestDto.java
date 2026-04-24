package com.bookshelf.idp.libraryservice.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BookRequestDto {
    private String title;
    private String description;
    private String imageUrl;
    private String isbn;
    private Integer totalCopies;
    private List<UUID> authorIds;
}