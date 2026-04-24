package com.bookshelf.idp.libraryservice.controller;

import com.bookshelf.idp.libraryservice.dto.LoanRequestDto;
import com.bookshelf.idp.libraryservice.dto.LoanResponseDto;
import com.bookshelf.idp.libraryservice.service.LoanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@SecurityRequirement(name = "Bearer Authentication")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponseDto> create(@RequestBody LoanRequestDto dto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(loanService.create(dto, userDetails.getUsername()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanResponseDto> returnBook(@PathVariable UUID id) {
        return new ResponseEntity<>(loanService.returnBook(id), HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(loanService.getMyLoans(userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LoanResponseDto>> getAll() {
        return new ResponseEntity<>(loanService.getAll(), HttpStatus.OK);
    }
}