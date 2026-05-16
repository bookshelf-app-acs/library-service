package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.client.DatabaseServiceClient;
import com.bookshelf.idp.libraryservice.dto.*;
import com.bookshelf.idp.libraryservice.exception.BadRequestException;
import com.bookshelf.idp.libraryservice.exception.NotFoundException;
import com.bookshelf.idp.libraryservice.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final DatabaseServiceClient dbClient;
    private final NotificationService notificationService;

    public LoanService(DatabaseServiceClient dbClient, NotificationService notificationService) {
        this.dbClient = dbClient;
        this.notificationService = notificationService;
    }

    public LoanResponseDto create(LoanRequestDto dto, String email) {
        UserModel user = dbClient.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        BookModel book = dbClient.findBookById(dto.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) throw new BadRequestException("Book is not available");

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        dbClient.updateBook(book.getId(), book);

        LoanModel loan = new LoanModel();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);

        LoanModel saved = dbClient.saveLoan(loan);

        notificationService.sendNotification(
                user.getEmail(),
                "LOAN_CONFIRMED",
                "Your loan for '" + book.getTitle() + "' has been confirmed. Due date: " + loan.getDueDate(),
                book.getId()
        );

        return toDto(saved);
    }

    public LoanResponseDto returnBook(UUID loanId) {
        LoanModel loan = dbClient.findLoanById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) throw new BadRequestException("Book already returned");

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        dbClient.updateLoan(loanId, loan);

        BookModel book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        dbClient.updateBook(book.getId(), book);

        dbClient.findFirstPendingReservationByBookId(book.getId())
                .ifPresent(reservation -> {
                    reservation.setStatus(ReservationStatus.FULFILLED);
                    dbClient.updateReservation(reservation.getId(), reservation);
                    notificationService.sendNotification(
                            reservation.getUser().getEmail(),
                            "RESERVATION_AVAILABLE",
                            "The book '" + book.getTitle() + "' you reserved is now available!",
                            book.getId()
                    );
                });

        return toDto(loan);
    }

    public List<LoanResponseDto> getMyLoans(String email) {
        UserModel user = dbClient.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return dbClient.findLoansByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<LoanResponseDto> getAll() {
        return dbClient.findAllLoans().stream().map(this::toDto).collect(Collectors.toList());
    }

    private LoanResponseDto toDto(LoanModel loan) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setId(loan.getId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus());

        BookModel b = loan.getBook();
        BookResponseDto bookDto = new BookResponseDto();
        bookDto.setId(b.getId());
        bookDto.setTitle(b.getTitle());
        bookDto.setIsbn(b.getIsbn());
        bookDto.setDescription(b.getDescription());
        bookDto.setAvailableCopies(b.getAvailableCopies());
        bookDto.setTotalCopies(b.getTotalCopies());
        bookDto.setImageUrl(b.getImageUrl());
        bookDto.setAuthors(b.getAuthors() == null ? List.of() : b.getAuthors().stream()
                .map(a -> new AuthorResponseDto(a.getId(), a.getFirstName(), a.getLastName()))
                .collect(Collectors.toList()));
        dto.setBook(bookDto);

        UserModel u = loan.getUser();
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(u.getId());
        userDto.setName(u.getName());
        userDto.setEmail(u.getEmail());
        userDto.setRole(u.getRole());
        dto.setUser(userDto);

        return dto;
    }
}