package nl.miwnn.ch19.vincent.LibraryDemo.model;

/**
 * @author Vincent Velthuizen
 * Information about a book for which the library might have a Copy
 */
public class Book {
    private String title;
    private String authorName;
    private int publicationYear;

    public Book(String title, String authorName, int publicationYear) {
        this.title = title;
        this.authorName = authorName;
        this.publicationYear = publicationYear;
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

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
}
