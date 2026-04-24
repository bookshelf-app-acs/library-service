package com.bookshelf.idp.libraryservice.repository;

import com.bookshelf.idp.libraryservice.entity.Reservation;
import com.bookshelf.idp.libraryservice.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUserId(UUID userId);
    List<Reservation> findByBookId(UUID bookId);
    List<Reservation> findByStatus(ReservationStatus status);
    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservationDateAsc(UUID bookId, ReservationStatus status);
}