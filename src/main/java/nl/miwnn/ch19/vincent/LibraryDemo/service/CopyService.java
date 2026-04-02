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

    public CopyService(CopyRepository copyRepository) {
        this.copyRepository = copyRepository;
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

    private Copy changeCopyState(Long copyId, LibraryUser borrower) {
        Copy copy = copyRepository.findById(copyId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Exemplaar met id %d bestaat niet.", copyId)));

        // TODO validation
//        if (copy.getAvailable() == newState) {
//            throw new IllegalStateException(String.format("Exemplaar is %s uitgeleend.", newState ? "al" : "niet"));
//        }

        copy.setBorrower(borrower);
        copyRepository.save(copy);
        return copy;
    }
}
