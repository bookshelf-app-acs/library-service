package com.bookshelf.idp.libraryservice.config.security;

import com.bookshelf.idp.libraryservice.client.DatabaseServiceClient;
import com.bookshelf.idp.libraryservice.model.AuthorModel;
import com.bookshelf.idp.libraryservice.model.BookModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ApplicationInitializer implements ApplicationRunner {

    private final DatabaseServiceClient dbClient;

    public ApplicationInitializer(DatabaseServiceClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            AuthorModel king = dbClient.findAuthorByName("Stephen", "King").orElseGet(() -> {
                AuthorModel a = new AuthorModel();
                a.setFirstName("Stephen");
                a.setLastName("King");
                return dbClient.saveAuthor(a);
            });

            if (dbClient.findBooksByTitle("The Shining").isEmpty()) {
                BookModel book = new BookModel();
                book.setTitle("The Shining");
                book.setDescription("A horror novel");
                book.setIsbn("978-0-385-12167-5");
                book.setTotalCopies(3);
                book.setAvailableCopies(3);
                book.setAuthors(List.of(king));
                dbClient.saveBook(book);
            }
        } catch (Exception e) {
            System.out.println("Database service not available, skipping initialization: " + e.getMessage());
        }
    }
}