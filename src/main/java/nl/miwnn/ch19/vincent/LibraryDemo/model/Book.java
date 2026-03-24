package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.validation.constraints.*;

/**
 * @author Vincent Velthuizen
 * Information about a book for which the library might have a Copy
 */
public class Book {

    @NotBlank(message = "Titel mag niet leeg zijn")
    @Size(max = 200, message = "Titel mag maximaal 200 tekens bevatten")
    private String title;

    @NotBlank(message = "Auteur mag niet leeg zijn")
    private String authorName;

    @NotNull(message = "Publicatiejaar is verplicht")
    @Min(value = 1000, message = "Publicatiejaar moet minimaal 1000 zijn")
    @Max(value = 2100, message = "Publicatiejaar mag maximaal 2100 zijn")
    private int publicationYear;

    public Book(String title, String authorName, int publicationYear) {
        this.title = title;
        this.authorName = authorName;
        this.publicationYear = publicationYear;
    }

    public Book() {}

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

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
}
