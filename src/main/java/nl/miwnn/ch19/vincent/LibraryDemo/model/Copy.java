package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.persistence.*;

/**
 * @author Vincent Velthuizen
 * A copy of a book, the library owns and that can be lent to users of the library
 */
@Entity
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    @JoinColumn(nullable = true)
    private LibraryUser borrower = null;

    public Copy(Book book) {
        this.book = book;
    }

    public Copy() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LibraryUser getBorrower() {
        return borrower;
    }

    public void setBorrower(LibraryUser borrower) {
        this.borrower = borrower;
    }

    public Boolean getAvailable() {
        return borrower == null;
    }
}
