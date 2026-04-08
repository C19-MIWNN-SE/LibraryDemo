package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import nl.miwnn.ch19.vincent.LibraryDemo.validation.UniqueGenreShortName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Velthuizen
 * A classification for books
 */
@Entity
@UniqueGenreShortName
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naam mag niet leeg zijn")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Afkorting mag niet leeg zijn")
    @Column(nullable = false, unique = true)
    private String shortName;

    @OneToMany(mappedBy = "genre")
    private List<Book> books = new ArrayList<>();

    @OneToOne
    private Book exampleBook;

    public Genre(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public Genre() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Book getExampleBook() {
        if (exampleBook == null && !books.isEmpty()) {
            return books.get(0);
        }
        return exampleBook;
    }

    public void setExampleBook(Book exampleBook) {
        this.exampleBook = exampleBook;
    }
}
