package com.bookshelf.idp.libraryservice.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NotificationRequestDto {
    private UUID userId;
    private String type;
    private String message;
    private UUID bookId;
}