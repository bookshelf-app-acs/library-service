package com.bookshelf.idp.libraryservice.controller;

import com.bookshelf.idp.libraryservice.dto.BookRequestDto;
import com.bookshelf.idp.libraryservice.dto.BookResponseDto;
import com.bookshelf.idp.libraryservice.service.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@SecurityRequirement(name = "Bearer Authentication")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAll() {
        return new ResponseEntity<>(bookService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(bookService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDto>> search(@RequestParam String title) {
        return new ResponseEntity<>(bookService.search(title), HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookResponseDto>> getAvailable() {
        return new ResponseEntity<>(bookService.getAvailable(), HttpStatus.OK);
    }

    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDto>> getByAuthor(@RequestParam String lastName) {
        return new ResponseEntity<>(bookService.getByAuthor(lastName), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BookResponseDto> create(@RequestBody BookRequestDto dto) {
        return new ResponseEntity<>(bookService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BookResponseDto> update(@PathVariable UUID id, @RequestBody BookRequestDto dto) {
        return new ResponseEntity<>(bookService.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}