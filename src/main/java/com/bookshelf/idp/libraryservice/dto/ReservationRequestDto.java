package com.bookshelf.idp.libraryservice.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReservationRequestDto {
    private UUID bookId;
}