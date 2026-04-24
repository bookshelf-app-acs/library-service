package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.dto.*;
import com.bookshelf.idp.libraryservice.entity.Author;
import com.bookshelf.idp.libraryservice.entity.Book;
import com.bookshelf.idp.libraryservice.exception.NotFoundException;
import com.bookshelf.idp.libraryservice.repository.AuthorRepository;
import com.bookshelf.idp.libraryservice.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookResponseDto> getAll() {
        return bookRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookResponseDto getById(UUID id) {
        return bookRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public List<BookResponseDto> search(String title) {
        return bookRepository.findByTitle(title).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookResponseDto> getAvailable() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookResponseDto> getByAuthor(String lastName) {
        return bookRepository.findByAuthorLastName(lastName).stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookResponseDto create(BookRequestDto dto) {
        List<Author> authors = dto.getAuthorIds().stream()
                .map(id -> authorRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Author not found: " + id)))
                .collect(Collectors.toList());

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setImageUrl(dto.getImageUrl());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());
        book.setAuthors(authors);
        return toDto(bookRepository.save(book));
    }

    public BookResponseDto update(UUID id, BookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<Author> authors = dto.getAuthorIds().stream()
                .map(authorId -> authorRepository.findById(authorId)
                        .orElseThrow(() -> new NotFoundException("Author not found: " + authorId)))
                .collect(Collectors.toList());

        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setImageUrl(dto.getImageUrl());
        book.setDescription(dto.getDescription());
        int diff = dto.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + diff);
        book.setAuthors(authors);
        return toDto(bookRepository.save(book));
    }

    public void delete(UUID id) {
        if (!bookRepository.existsById(id)) throw new NotFoundException("Book not found");
        bookRepository.deleteById(id);
    }

    private BookResponseDto toDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setImageUrl(book.getImageUrl());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setAuthors(book.getAuthors().stream()
                .map(a -> {
                    AuthorResponseDto authorDto = new AuthorResponseDto();
                    authorDto.setId(a.getId());
                    authorDto.setFirstName(a.getFirstName());
                    authorDto.setLastName(a.getLastName());
                    return authorDto;
                }).collect(Collectors.toList()));
        return dto;
    }
}