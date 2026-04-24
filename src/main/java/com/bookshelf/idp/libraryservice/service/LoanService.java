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
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository,
                       UserRepository userRepository, ReservationRepository reservationRepository,
                       NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
    }

    public LoanResponseDto create(LoanRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) throw new BadRequestException("Book is not available");

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);

        LoanResponseDto response = toDto(loanRepository.save(loan));

        notificationService.sendNotification(
                user.getId(),
                "LOAN_CONFIRMED",
                "Your loan for '" + book.getTitle() + "' has been confirmed. Due date: " + loan.getDueDate(),
                book.getId()
        );

        return response;
    }

    public LoanResponseDto returnBook(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) throw new BadRequestException("Book already returned");

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        reservationRepository.findFirstByBookIdAndStatusOrderByReservationDateAsc(
                        book.getId(), ReservationStatus.PENDING)
                .ifPresent(reservation -> {
                    reservation.setStatus(ReservationStatus.FULFILLED);
                    reservationRepository.save(reservation);
                    notificationService.sendNotification(
                            reservation.getUser().getId(),
                            "RESERVATION_AVAILABLE",
                            "The book '" + book.getTitle() + "' you reserved is now available!",
                            book.getId()
                    );
                });

        return toDto(loanRepository.save(loan));
    }

    public List<LoanResponseDto> getMyLoans(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return loanRepository.findByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<LoanResponseDto> getAll() {
        return loanRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private LoanResponseDto toDto(Loan loan) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setId(loan.getId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus());

        BookResponseDto bookDto = new BookResponseDto();
        bookDto.setId(loan.getBook().getId());
        bookDto.setTitle(loan.getBook().getTitle());
        bookDto.setIsbn(loan.getBook().getIsbn());
        bookDto.setDescription(loan.getBook().getDescription());
        bookDto.setAvailableCopies(loan.getBook().getAvailableCopies());
        bookDto.setTotalCopies(loan.getBook().getTotalCopies());
        bookDto.setImageUrl(loan.getBook().getImageUrl());
        bookDto.setAuthors(loan.getBook().getAuthors().stream()
                .map(a -> {
                    AuthorResponseDto authorDto = new AuthorResponseDto();
                    authorDto.setId(a.getId());
                    authorDto.setFirstName(a.getFirstName());
                    authorDto.setLastName(a.getLastName());
                    return authorDto;
                }).collect(Collectors.toList()));
        dto.setBook(bookDto);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(loan.getUser().getId());
        userDto.setName(loan.getUser().getName());
        userDto.setEmail(loan.getUser().getEmail());
        userDto.setRole(loan.getUser().getRole().name());
        dto.setUser(userDto);

        return dto;
    }
}