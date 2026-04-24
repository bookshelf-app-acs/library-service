package com.bookshelf.idp.libraryservice.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AuthorRequestDto {
    private String firstName;
    private String lastName;
}