package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

/**
 * @author Vincent Velthuizen
 * Information about a book for which the library might have a Copy
 */
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel mag niet leeg zijn")
    @Size(max = 200, message = "Titel mag maximaal 200 tekens bevatten")
    private String title;

    @NotBlank(message = "Auteur mag niet leeg zijn")
    private String authorName;

    @NotNull(message = "Publicatiejaar is verplicht")
    @Min(value = 1000, message = "Publicatiejaar moet minimaal 1000 zijn")
    @Max(value = 2100, message = "Publicatiejaar mag maximaal 2100 zijn")
    private Integer publicationYear;

    public Book(String title, String authorName, Integer publicationYear) {
        this.title = title;
        this.authorName = authorName;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
}
