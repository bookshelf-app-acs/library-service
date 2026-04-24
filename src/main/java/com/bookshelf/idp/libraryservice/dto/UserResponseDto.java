package com.bookshelf.idp.libraryservice.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String role;
}