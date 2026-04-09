package nl.miwnn.ch19.vincent.LibraryDemo.service;

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.CopyRepository;
import org.springframework.stereotype.Service;

/**
 * @author Vincent Velthuizen
 * Handle all business logic regarding copies
 */
@Service
public class CopyService {

    private final CopyRepository copyRepository;
    private final LoanService loanService;

    public CopyService(CopyRepository copyRepository, LoanService loanService) {
        this.copyRepository = copyRepository;
        this.loanService = loanService;
    }

    public Copy findById(Long copyId) {
        return copyRepository.findById(copyId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exemplaar met id %d bestaat niet.", copyId)));
    }

    public void borrowCopy(Long copyId, LibraryUser borrower) {
        changeCopyState(copyId, borrower);
    }

    public void returnCopy(Long copyId) {
        changeCopyState(copyId, null);
    }

    private void changeCopyState(Long copyId, LibraryUser borrower) {
        Copy copy = copyRepository.findById(copyId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exemplaar met id %d bestaat niet.", copyId)));

        if (borrower != null) {
            copy.setAvailable(false);
        } else {
            copy.setAvailable(true);
        }

        copyRepository.save(copy);

        if (borrower != null) {
            loanService.startLoan(copy, borrower);
        } else {
            loanService.closeLoan(copy);
        }

    }
}
