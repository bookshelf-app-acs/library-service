package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.dto.AuthorRequestDto;
import com.bookshelf.idp.libraryservice.dto.AuthorResponseDto;
import com.bookshelf.idp.libraryservice.entity.Author;
import com.bookshelf.idp.libraryservice.exception.BadRequestException;
import com.bookshelf.idp.libraryservice.exception.NotFoundException;
import com.bookshelf.idp.libraryservice.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponseDto> getAll() {
        return authorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorResponseDto getById(UUID id) {
        return authorRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Author not found"));
    }

    public List<AuthorResponseDto> getByBookId(UUID bookId) {
        return authorRepository.findByBookId(bookId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorResponseDto create(AuthorRequestDto dto) {
        if (authorRepository.findByFirstNameAndLastName(dto.getFirstName(), dto.getLastName()).isPresent()) {
            throw new BadRequestException("Author already exists");
        }
        Author author = new Author();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        return toDto(authorRepository.save(author));
    }

    public AuthorResponseDto update(UUID id, AuthorRequestDto dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        return toDto(authorRepository.save(author));
    }

    public void delete(UUID id) {
        if (!authorRepository.existsById(id)) throw new NotFoundException("Author not found");
        authorRepository.deleteById(id);
    }

    private AuthorResponseDto toDto(Author author) {
        AuthorResponseDto dto = new AuthorResponseDto();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        return dto;
    }
}