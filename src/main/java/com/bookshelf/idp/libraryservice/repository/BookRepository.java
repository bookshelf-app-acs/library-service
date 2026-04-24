package com.bookshelf.idp.libraryservice.repository;

import com.bookshelf.idp.libraryservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByTitle(String title);
    List<Book> findByAvailableCopiesGreaterThan(int copies);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.lastName = :lastName")
    List<Book> findByAuthorLastName(@Param("lastName") String lastName);
}