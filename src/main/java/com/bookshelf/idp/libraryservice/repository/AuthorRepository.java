package com.bookshelf.idp.libraryservice.repository;

import com.bookshelf.idp.libraryservice.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT a FROM Author a JOIN a.books b WHERE b.id = :bookId")
    List<Author> findByBookId(@Param("bookId") UUID bookId);
}