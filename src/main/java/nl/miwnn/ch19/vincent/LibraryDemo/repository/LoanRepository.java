package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Velthuizen
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByCopyAndReturnDateIsNull(Copy copy);

    List<Loan> findByReturnDateIsNull();

    List<Loan> findByBorrowerAndReturnDateIsNull(LibraryUser borrower);

    List<Loan> findByBorrowerAndReturnDateIsNotNullOrderByReturnDateDesc(LibraryUser borrower, Pageable pageable);

    boolean existsByCopyBookAndBorrowerAndReturnDateIsNull(Book copyBook, LibraryUser borrower);

    List<Loan> findByCopyBookAndReturnDateIsNull(Book book);

}
