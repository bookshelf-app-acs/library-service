package com.bookshelf.idp.libraryservice.repository;

import com.bookshelf.idp.libraryservice.entity.Loan;
import com.bookshelf.idp.libraryservice.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserId(UUID userId);
    List<Loan> findByBookId(UUID bookId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(UUID userId, LoanStatus status);
}