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
public class ReservationService {

    private final DatabaseServiceClient dbClient;

    public ReservationService(DatabaseServiceClient dbClient) {
        this.dbClient = dbClient;
    }

    public ReservationResponseDto create(ReservationRequestDto dto, String email) {
        UserModel user = dbClient.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        BookModel book = dbClient.findBookById(dto.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        if (book.getAvailableCopies() > 0) throw new BadRequestException("Book is available, borrow it directly");

        boolean alreadyReserved = dbClient.findReservationsByUserId(user.getId()).stream()
                .anyMatch(r -> r.getBook().getId().equals(book.getId())
                        && r.getStatus() == ReservationStatus.PENDING);

        if (alreadyReserved) throw new BadRequestException("You already have a pending reservation for this book");

        ReservationModel reservation = new ReservationModel();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.PENDING);

        return toDto(dbClient.saveReservation(reservation));
    }

    public ReservationResponseDto cancel(UUID id) {
        ReservationModel reservation = dbClient.findReservationById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.PENDING)
            throw new BadRequestException("Only pending reservations can be cancelled");
        reservation.setStatus(ReservationStatus.CANCELLED);
        return toDto(dbClient.updateReservation(id, reservation));
    }

    public List<ReservationResponseDto> getMyReservations(String email) {
        UserModel user = dbClient.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return dbClient.findReservationsByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ReservationResponseDto> getAll() {
        return dbClient.findAllReservations().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ReservationResponseDto toDto(ReservationModel reservation) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(reservation.getId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());

        BookModel b = reservation.getBook();
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

        UserModel u = reservation.getUser();
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(u.getId());
        userDto.setName(u.getName());
        userDto.setEmail(u.getEmail());
        userDto.setRole(u.getRole());
        dto.setUser(userDto);

        return dto;
    }
}