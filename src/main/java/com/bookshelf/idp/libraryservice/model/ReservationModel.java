package com.bookshelf.idp.libraryservice.model;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReservationModel {
    private UUID id;
    private UserModel user;
    private BookModel book;
    private LocalDate reservationDate;
    private ReservationStatus status;
}