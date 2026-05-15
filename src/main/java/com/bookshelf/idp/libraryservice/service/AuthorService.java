package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.client.DatabaseServiceClient;
import com.bookshelf.idp.libraryservice.dto.AuthorRequestDto;
import com.bookshelf.idp.libraryservice.dto.AuthorResponseDto;
import com.bookshelf.idp.libraryservice.exception.BadRequestException;
import com.bookshelf.idp.libraryservice.exception.NotFoundException;
import com.bookshelf.idp.libraryservice.model.AuthorModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final DatabaseServiceClient dbClient;

    public AuthorService(DatabaseServiceClient dbClient) {
        this.dbClient = dbClient;
    }

    public List<AuthorResponseDto> getAll() {
        return dbClient.findAllAuthors().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorResponseDto getById(UUID id) {
        return dbClient.findAuthorById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Author not found"));
    }

    public List<AuthorResponseDto> getByBookId(UUID bookId) {
        return dbClient.findAuthorsByBookId(bookId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorResponseDto create(AuthorRequestDto dto) {
        if (dbClient.findAuthorByName(dto.getFirstName(), dto.getLastName()).isPresent()) {
            throw new BadRequestException("Author already exists");
        }
        AuthorModel author = new AuthorModel();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        return toDto(dbClient.saveAuthor(author));
    }

    public AuthorResponseDto update(UUID id, AuthorRequestDto dto) {
        AuthorModel author = dbClient.findAuthorById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        return toDto(dbClient.updateAuthor(id, author));
    }

    public void delete(UUID id) {
        if (!dbClient.authorExistsById(id)) throw new NotFoundException("Author not found");
        dbClient.deleteAuthor(id);
    }

    private AuthorResponseDto toDto(AuthorModel author) {
        AuthorResponseDto dto = new AuthorResponseDto();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        return dto;
    }
}