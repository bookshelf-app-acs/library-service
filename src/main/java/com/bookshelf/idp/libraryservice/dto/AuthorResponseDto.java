package com.bookshelf.idp.libraryservice.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AuthorResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
}