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
            // ─── Authors ───────────────────────────────────────
            AuthorModel king = ensureAuthor("Stephen", "King");
            AuthorModel rowling = ensureAuthor("J.K.", "Rowling");
            AuthorModel orwell = ensureAuthor("George", "Orwell");
            AuthorModel christie = ensureAuthor("Agatha", "Christie");
            AuthorModel sadoveanu = ensureAuthor("Mihail", "Sadoveanu");
            AuthorModel tolkien = ensureAuthor("J.R.R.", "Tolkien");

            // ─── Books ─────────────────────────────────────────
            ensureBook("The Shining", "A horror novel about a haunted hotel.",
                    "978-0-385-12167-5", 3, List.of(king));
            ensureBook("It", "A child-eating shape-shifting entity terrorizes Derry.",
                    "978-1-501-14211-0", 2, List.of(king));
            ensureBook("Harry Potter and the Philosopher's Stone",
                    "The boy wizard begins his journey at Hogwarts.",
                    "978-0-7475-3269-9", 5, List.of(rowling));
            ensureBook("Harry Potter and the Chamber of Secrets",
                    "Harry returns to Hogwarts for his second year.",
                    "978-0-7475-3849-3", 4, List.of(rowling));
            ensureBook("1984", "A dystopian novel about totalitarianism.",
                    "978-0-452-28423-4", 3, List.of(orwell));
            ensureBook("Animal Farm", "An allegorical fable about Soviet Russia.",
                    "978-0-452-28424-1", 2, List.of(orwell));
            ensureBook("Murder on the Orient Express",
                    "Hercule Poirot solves a murder on a snowbound train.",
                    "978-0-00-711931-8", 3, List.of(christie));
            ensureBook("And Then There Were None",
                    "Ten strangers are lured to an island and killed one by one.",
                    "978-0-06-207348-8", 2, List.of(christie));
            ensureBook("Baltagul", "Roman classic românesc despre răzbunare.",
                    "978-973-23-2456-7", 4, List.of(sadoveanu));
            ensureBook("The Hobbit", "Bilbo Baggins goes on an unexpected journey.",
                    "978-0-547-92822-7", 3, List.of(tolkien));
            ensureBook("The Lord of the Rings", "The epic quest to destroy the One Ring.",
                    "978-0-544-00341-5", 2, List.of(tolkien));

        } catch (Exception e) {
            System.out.println("Database service not available, skipping initialization: " + e.getMessage());
        }
    }

    private AuthorModel ensureAuthor(String firstName, String lastName) {
        return dbClient.findAuthorByName(firstName, lastName).orElseGet(() -> {
            AuthorModel a = new AuthorModel();
            a.setFirstName(firstName);
            a.setLastName(lastName);
            return dbClient.saveAuthor(a);
        });
    }

    private void ensureBook(String title, String description, String isbn,
                             int copies, List<AuthorModel> authors) {
        if (dbClient.findBooksByTitle(title).isEmpty()) {
            BookModel book = new BookModel();
            book.setTitle(title);
            book.setDescription(description);
            book.setIsbn(isbn);
            book.setTotalCopies(copies);
            book.setAvailableCopies(copies);
            book.setAuthors(authors);
            dbClient.saveBook(book);
        }
    }
}
