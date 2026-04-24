package com.bookshelf.idp.libraryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "authors", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books;
}