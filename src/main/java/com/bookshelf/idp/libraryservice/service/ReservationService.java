package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.dto.*;
import com.bookshelf.idp.libraryservice.entity.*;
import com.bookshelf.idp.libraryservice.exception.BadRequestException;
import com.bookshelf.idp.libraryservice.exception.NotFoundException;
import com.bookshelf.idp.libraryservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public ReservationResponseDto create(ReservationRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        if (book.getAvailableCopies() > 0) throw new BadRequestException("Book is available, borrow it directly");

        boolean alreadyReserved = reservationRepository.findByUserId(user.getId()).stream()
                .anyMatch(r -> r.getBook().getId().equals(book.getId()) && r.getStatus() == ReservationStatus.PENDING);

        if (alreadyReserved) throw new BadRequestException("You already have a pending reservation for this book");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.PENDING);

        return toDto(reservationRepository.save(reservation));
    }

    public ReservationResponseDto cancel(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.PENDING)
            throw new BadRequestException("Only pending reservations can be cancelled");
        reservation.setStatus(ReservationStatus.CANCELLED);
        return toDto(reservationRepository.save(reservation));
    }

    public List<ReservationResponseDto> getMyReservations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return reservationRepository.findByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ReservationResponseDto> getAll() {
        return reservationRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ReservationResponseDto toDto(Reservation reservation) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(reservation.getId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());

        BookResponseDto bookDto = new BookResponseDto();
        bookDto.setId(reservation.getBook().getId());
        bookDto.setTitle(reservation.getBook().getTitle());
        bookDto.setIsbn(reservation.getBook().getIsbn());
        bookDto.setDescription(reservation.getBook().getDescription());
        bookDto.setAvailableCopies(reservation.getBook().getAvailableCopies());
        bookDto.setTotalCopies(reservation.getBook().getTotalCopies());
        bookDto.setImageUrl(reservation.getBook().getImageUrl());
        bookDto.setAuthors(reservation.getBook().getAuthors().stream()
                .map(a -> {
                    AuthorResponseDto authorDto = new AuthorResponseDto();
                    authorDto.setId(a.getId());
                    authorDto.setFirstName(a.getFirstName());
                    authorDto.setLastName(a.getLastName());
                    return authorDto;
                }).collect(Collectors.toList()));
        dto.setBook(bookDto);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(reservation.getUser().getId());
        userDto.setName(reservation.getUser().getName());
        userDto.setEmail(reservation.getUser().getEmail());
        userDto.setRole(reservation.getUser().getRole().name());
        dto.setUser(userDto);

        return dto;
    }
}