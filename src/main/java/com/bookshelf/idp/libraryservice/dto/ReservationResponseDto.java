package com.bookshelf.idp.libraryservice.dto;

import com.bookshelf.idp.libraryservice.entity.ReservationStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReservationResponseDto {
    private UUID id;
    private BookResponseDto book;
    private UserResponseDto user;
    private LocalDate reservationDate;
    private ReservationStatus status;
}