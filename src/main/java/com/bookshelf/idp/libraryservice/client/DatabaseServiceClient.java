package com.bookshelf.idp.libraryservice.client;

import com.bookshelf.idp.libraryservice.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DatabaseServiceClient {

    private final RestTemplate restTemplate;

    @Value("${database.service.url}")
    private String dbUrl;

    public DatabaseServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ── USERS ──
    public Optional<UserModel> findUserByEmail(String email) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(dbUrl + "/internal/users/email/" + email, UserModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public Optional<UserModel> findUserById(UUID id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(dbUrl + "/internal/users/" + id, UserModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    // ── AUTHORS ──
    public List<AuthorModel> findAllAuthors() {
        AuthorModel[] authors = restTemplate.getForObject(dbUrl + "/internal/authors", AuthorModel[].class);
        return authors != null ? Arrays.asList(authors) : List.of();
    }

    public Optional<AuthorModel> findAuthorById(UUID id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(dbUrl + "/internal/authors/" + id, AuthorModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<AuthorModel> findAuthorsByBookId(UUID bookId) {
        AuthorModel[] authors = restTemplate.getForObject(dbUrl + "/internal/authors/book/" + bookId, AuthorModel[].class);
        return authors != null ? Arrays.asList(authors) : List.of();
    }

    public Optional<AuthorModel> findAuthorByName(String firstName, String lastName) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(
                    dbUrl + "/internal/authors/search?firstName=" + firstName + "&lastName=" + lastName, AuthorModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public AuthorModel saveAuthor(AuthorModel author) {
        return restTemplate.postForObject(dbUrl + "/internal/authors", author, AuthorModel.class);
    }

    public AuthorModel updateAuthor(UUID id, AuthorModel author) {
        ResponseEntity<AuthorModel> response = restTemplate.exchange(
                dbUrl + "/internal/authors/" + id, HttpMethod.PUT,
                new HttpEntity<>(author), AuthorModel.class);
        return response.getBody();
    }

    public boolean authorExistsById(UUID id) {
        return findAuthorById(id).isPresent();
    }

    public void deleteAuthor(UUID id) {
        restTemplate.delete(dbUrl + "/internal/authors/" + id);
    }

    // ── BOOKS ──
    public List<BookModel> findAllBooks() {
        BookModel[] books = restTemplate.getForObject(dbUrl + "/internal/books", BookModel[].class);
        return books != null ? Arrays.asList(books) : List.of();
    }

    public Optional<BookModel> findBookById(UUID id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(dbUrl + "/internal/books/" + id, BookModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<BookModel> findBooksByTitle(String title) {
        BookModel[] books = restTemplate.getForObject(dbUrl + "/internal/books/search?title=" + title, BookModel[].class);
        return books != null ? Arrays.asList(books) : List.of();
    }

    public List<BookModel> findAvailableBooks() {
        BookModel[] books = restTemplate.getForObject(dbUrl + "/internal/books/available", BookModel[].class);
        return books != null ? Arrays.asList(books) : List.of();
    }

    public List<BookModel> findBooksByAuthorLastName(String lastName) {
        BookModel[] books = restTemplate.getForObject(dbUrl + "/internal/books/author?lastName=" + lastName, BookModel[].class);
        return books != null ? Arrays.asList(books) : List.of();
    }

    public BookModel saveBook(BookModel book) {
        return restTemplate.postForObject(dbUrl + "/internal/books", book, BookModel.class);
    }

    public BookModel updateBook(UUID id, BookModel book) {
        ResponseEntity<BookModel> response = restTemplate.exchange(
                dbUrl + "/internal/books/" + id, HttpMethod.PUT,
                new HttpEntity<>(book), BookModel.class);
        return response.getBody();
    }

    public boolean bookExistsById(UUID id) {
        return findBookById(id).isPresent();
    }

    public void deleteBook(UUID id) {
        restTemplate.delete(dbUrl + "/internal/books/" + id);
    }

    // ── LOANS ──
    public List<LoanModel> findAllLoans() {
        LoanModel[] loans = restTemplate.getForObject(dbUrl + "/internal/loans", LoanModel[].class);
        return loans != null ? Arrays.asList(loans) : List.of();
    }

    public Optional<LoanModel> findLoanById(UUID id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(dbUrl + "/internal/loans/" + id, LoanModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<LoanModel> findLoansByUserId(UUID userId) {
        LoanModel[] loans = restTemplate.getForObject(dbUrl + "/internal/loans/user/" + userId, LoanModel[].class);
        return loans != null ? Arrays.asList(loans) : List.of();
    }

    public LoanModel saveLoan(LoanModel loan) {
        return restTemplate.postForObject(dbUrl + "/internal/loans", loan, LoanModel.class);
    }

    public LoanModel updateLoan(UUID id, LoanModel loan) {
        ResponseEntity<LoanModel> response = restTemplate.exchange(
                dbUrl + "/internal/loans/" + id, HttpMethod.PUT,
                new HttpEntity<>(loan), LoanModel.class);
        return response.getBody();
    }

    // ── RESERVATIONS ──
    public List<ReservationModel> findAllReservations() {
        ReservationModel[] reservations = restTemplate.getForObject(dbUrl + "/internal/reservations", ReservationModel[].class);
        return reservations != null ? Arrays.asList(reservations) : List.of();
    }

    public Optional<ReservationModel> findReservationById(UUID id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(dbUrl + "/internal/reservations/" + id, ReservationModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<ReservationModel> findReservationsByUserId(UUID userId) {
        ReservationModel[] reservations = restTemplate.getForObject(dbUrl + "/internal/reservations/user/" + userId, ReservationModel[].class);
        return reservations != null ? Arrays.asList(reservations) : List.of();
    }

    public Optional<ReservationModel> findFirstPendingReservationByBookId(UUID bookId) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(
                    dbUrl + "/internal/reservations/book/" + bookId + "/first-pending", ReservationModel.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public ReservationModel saveReservation(ReservationModel reservation) {
        return restTemplate.postForObject(dbUrl + "/internal/reservations", reservation, ReservationModel.class);
    }

    public ReservationModel updateReservation(UUID id, ReservationModel reservation) {
        ResponseEntity<ReservationModel> response = restTemplate.exchange(
                dbUrl + "/internal/reservations/" + id, HttpMethod.PUT,
                new HttpEntity<>(reservation), ReservationModel.class);
        return response.getBody();
    }
}