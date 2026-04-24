package com.bookshelf.idp.libraryservice.controller;

import com.bookshelf.idp.libraryservice.dto.ReservationRequestDto;
import com.bookshelf.idp.libraryservice.dto.ReservationResponseDto;
import com.bookshelf.idp.libraryservice.service.ReservationService;
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
@RequestMapping("/api/v1/reservations")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> create(@RequestBody ReservationRequestDto dto,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(reservationService.create(dto, userDetails.getUsername()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponseDto> cancel(@PathVariable UUID id) {
        return new ResponseEntity<>(reservationService.cancel(id), HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(reservationService.getMyReservations(userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReservationResponseDto>> getAll() {
        return new ResponseEntity<>(reservationService.getAll(), HttpStatus.OK);
    }
}