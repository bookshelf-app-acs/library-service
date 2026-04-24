package com.bookshelf.idp.libraryservice.config.security;

import com.bookshelf.idp.libraryservice.entity.*;
import com.bookshelf.idp.libraryservice.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ApplicationInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public ApplicationInitializer(UserRepository userRepository, AuthorRepository authorRepository,
                                  BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (authorRepository.findByFirstNameAndLastName("Stephen", "King").isEmpty()) {
            Author king = new Author();
            king.setFirstName("Stephen");
            king.setLastName("King");
            authorRepository.save(king);
        }

        if (bookRepository.findByTitle("The Shining").isEmpty()) {
            Author king = authorRepository.findByFirstNameAndLastName("Stephen", "King").orElseThrow();
            Book book = new Book();
            book.setTitle("The Shining");
            book.setDescription("A horror novel");
            book.setIsbn("978-0-385-12167-5");
            book.setTotalCopies(3);
            book.setAvailableCopies(3);
            book.setAuthors(List.of(king));
            bookRepository.save(book);
        }
    }
}