package com.bookshelf.idp.libraryservice.model;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookModel {
    private UUID id;
    private String title;
    private String isbn;
    private String description;
    private Integer totalCopies;
    private Integer availableCopies;
    private String imageUrl;
    private List<AuthorModel> authors;
}