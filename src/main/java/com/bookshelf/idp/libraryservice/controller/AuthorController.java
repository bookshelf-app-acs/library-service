package com.bookshelf.idp.libraryservice.controller;

import com.bookshelf.idp.libraryservice.dto.AuthorRequestDto;
import com.bookshelf.idp.libraryservice.dto.AuthorResponseDto;
import com.bookshelf.idp.libraryservice.service.AuthorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> getAll() {
        return new ResponseEntity<>(authorService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(authorService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<AuthorResponseDto>> getByBookId(@PathVariable UUID bookId) {
        return new ResponseEntity<>(authorService.getByBookId(bookId), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AuthorResponseDto> create(@RequestBody AuthorRequestDto dto) {
        return new ResponseEntity<>(authorService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AuthorResponseDto> update(@PathVariable UUID id, @RequestBody AuthorRequestDto dto) {
        return new ResponseEntity<>(authorService.update(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        authorService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}