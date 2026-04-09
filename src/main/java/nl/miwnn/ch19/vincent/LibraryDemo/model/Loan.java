package nl.miwnn.ch19.vincent.LibraryDemo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author Vincent Velthuizen
 * An instance of a LibraryUser taking home a Copy of a Book
 */
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Copy copy;

    @ManyToOne
    @JoinColumn(nullable = false)
    private LibraryUser borrower;

    @Column(nullable = false)
    private LocalDate borrowDate;

    @Column(nullable = true)
    private LocalDate returnDate;

    public Loan(Copy copy, LibraryUser borrower) {
        this.copy = copy;
        this.borrower = borrower;
        borrowDate = LocalDate.now();
    }

    public Loan() {
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public long getDaysOut() {
        LocalDate end = isActive() ? LocalDate.now() : returnDate;
        return ChronoUnit.DAYS.between(borrowDate, end);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Copy getCopy() {
        return copy;
    }

    public void setCopy(Copy copy) {
        this.copy = copy;
    }

    public LibraryUser getBorrower() {
        return borrower;
    }

    public void setBorrower(LibraryUser borrower) {
        this.borrower = borrower;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
