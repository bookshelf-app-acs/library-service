package com.bookshelf.idp.libraryservice.model;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthorModel {
    private UUID id;
    private String firstName;
    private String lastName;
}