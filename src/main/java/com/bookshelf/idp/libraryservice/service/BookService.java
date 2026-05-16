package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.client.DatabaseServiceClient;
import com.bookshelf.idp.libraryservice.dto.*;
import com.bookshelf.idp.libraryservice.exception.NotFoundException;
import com.bookshelf.idp.libraryservice.model.AuthorModel;
import com.bookshelf.idp.libraryservice.model.BookModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final DatabaseServiceClient dbClient;

    public BookService(DatabaseServiceClient dbClient) {
        this.dbClient = dbClient;
    }

    public List<BookResponseDto> getAll() {
        return dbClient.findAllBooks().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookResponseDto> findBooks(String title, UUID authorId, Boolean available) {
        return dbClient.findAllBooks().stream()
                .filter(b -> title == null || title.isBlank()
                        || b.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(b -> authorId == null
                        || (b.getAuthors() != null && b.getAuthors().stream()
                                .anyMatch(a -> authorId.equals(a.getId()))))
                .filter(b -> available == null || !available
                        || (b.getAvailableCopies() != null && b.getAvailableCopies() > 0))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BookResponseDto getById(UUID id) {
        return dbClient.findBookById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public List<BookResponseDto> search(String title) {
        return dbClient.findBooksByTitle(title).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookResponseDto> getAvailable() {
        return dbClient.findAvailableBooks().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookResponseDto> getByAuthor(String lastName) {
        return dbClient.findBooksByAuthorLastName(lastName).stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookResponseDto create(BookRequestDto dto) {
        List<AuthorModel> authors = dto.getAuthorIds().stream()
                .map(id -> dbClient.findAuthorById(id)
                        .orElseThrow(() -> new NotFoundException("Author not found: " + id)))
                .collect(Collectors.toList());

        BookModel book = new BookModel();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setImageUrl(dto.getImageUrl());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());
        book.setAuthors(authors);
        return toDto(dbClient.saveBook(book));
    }

    public BookResponseDto update(UUID id, BookRequestDto dto) {
        BookModel book = dbClient.findBookById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<AuthorModel> authors = dto.getAuthorIds().stream()
                .map(authorId -> dbClient.findAuthorById(authorId)
                        .orElseThrow(() -> new NotFoundException("Author not found: " + authorId)))
                .collect(Collectors.toList());

        int diff = dto.getTotalCopies() - book.getTotalCopies();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setImageUrl(dto.getImageUrl());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + diff);
        book.setAuthors(authors);
        return toDto(dbClient.updateBook(id, book));
    }

    public void delete(UUID id) {
        if (!dbClient.bookExistsById(id)) throw new NotFoundException("Book not found");
        dbClient.deleteBook(id);
    }

    private BookResponseDto toDto(BookModel book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setImageUrl(book.getImageUrl());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setAuthors(book.getAuthors() == null ? List.of() : book.getAuthors().stream()
                .map(a -> new AuthorResponseDto(a.getId(), a.getFirstName(), a.getLastName()))
                .collect(Collectors.toList()));
        return dto;
    }
}