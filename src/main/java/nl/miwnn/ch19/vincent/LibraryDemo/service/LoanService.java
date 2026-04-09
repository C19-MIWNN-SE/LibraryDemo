package nl.miwnn.ch19.vincent.LibraryDemo.service;

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Loan;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Vincent Velthuizen
 * Handle all businesslogic for Loans
 */
@Service
public class LoanService {
    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public void startLoan(Copy copy, LibraryUser borrower) {
        if (loanRepository.findByCopyAndReturnDateIsNull(copy).isPresent()) {
            throw new IllegalStateException("Dit exemplaar is op dit moment al uitgeleend.");
        }

        if (loanRepository.existsByCopyBookAndBorrowerAndReturnDateIsNull(copy.getBook(), borrower)) {
            throw new IllegalStateException("Gebruiker heeft al een actieve lening voor dit boek.");
        }

        log.info("Lening gestart: exemplaar {} door gebruiker {}", copy.getId(), borrower.getUsername());
        loanRepository.save(new Loan(copy, borrower));
    }

    public void closeLoan(Copy copy) {
        Loan loan = loanRepository.findByCopyAndReturnDateIsNull(copy).orElseThrow(() ->
                new EntityNotFoundException("No active loan found for copy " + copy.getId()));

        loan.setReturnDate(LocalDate.now());
        log.info("Lening afgesloten: exemplaar {} na {} dag(en)", copy.getId(), loan.getDaysOut());
        loanRepository.save(loan);
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findByReturnDateIsNull();
    }

    public List<Loan> getActiveLoansForUser(LibraryUser borrower) {
        return loanRepository.findByBorrowerAndReturnDateIsNull(borrower);
    }

    public List<Loan> getRecentlyClosedLoansForUser(LibraryUser borrower) {
        return loanRepository
                .findByBorrowerAndReturnDateIsNotNullOrderByReturnDateDesc(
                        borrower,
                        PageRequest.of(0, 6));
    }

    public Map<Long, Loan> getActiveLoanMapForBook(Book book) {
        return loanRepository.findByCopyBookAndReturnDateIsNull(book).stream()
                .collect(Collectors.toMap(loan -> loan.getCopy().getId(), loan -> loan));
    }
}
