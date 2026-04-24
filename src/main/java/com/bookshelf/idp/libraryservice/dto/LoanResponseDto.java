package com.bookshelf.idp.libraryservice.dto;

import com.bookshelf.idp.libraryservice.entity.LoanStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoanResponseDto {
    private UUID id;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private BookResponseDto book;
    private UserResponseDto user;
}