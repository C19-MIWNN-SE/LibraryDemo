package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import nl.miwnn.ch19.vincent.LibraryDemo.validation.UniqueBookTitle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Velthuizen
 * Information about a book for which the library might have a Copy
 */
@Entity
@UniqueBookTitle
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel mag niet leeg zijn")
    @Size(max = 200, message = "Titel mag maximaal 200 tekens bevatten")
    @Column(unique = true)
    private String title;

    @ManyToOne
    private Genre genre;

    @ManyToMany
    private List<Author> authors = new ArrayList<>();

    @Min(value = 1000, message = "Publicatiejaar moet minimaal 1000 zijn")
    @Max(value = 2100, message = "Publicatiejaar mag maximaal 2100 zijn")
    @Column(nullable = true)
    private Integer publicationYear;

    @Column(nullable = true, length = 2000)
    private String description;

    @Column(nullable = true)
    private String coverImageUrl;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Copy> copies = new ArrayList<>();

    public Book(String title, Integer publicationYear) {
        this.title = title;
        this.publicationYear = publicationYear;
    }

    public Book() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public List<Copy> getCopies() {
        return copies;
    }

    public void setCopies(List<Copy> copies) {
        this.copies = copies;
    }

    public int getNumberOfCopies() {
        return copies.size();
    }

    public long getAvailableNumberOfCopies() {
        return copies.stream().filter(Copy::getAvailable).count();
    }
}
