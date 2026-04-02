package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    private LocalDateTime borrowedAt;

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

    public LocalDateTime getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(LocalDateTime borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public long getDaysOut() {
        if (borrowedAt == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(borrowedAt, LocalDateTime.now());
    }

    public Boolean getAvailable() {
        return borrower == null;
    }
}
