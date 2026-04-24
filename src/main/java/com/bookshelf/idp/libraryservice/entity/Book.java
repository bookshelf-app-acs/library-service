package com.bookshelf.idp.libraryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "description")
    private String description;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}