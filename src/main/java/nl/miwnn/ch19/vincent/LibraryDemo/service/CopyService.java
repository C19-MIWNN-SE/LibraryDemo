package nl.miwnn.ch19.vincent.LibraryDemo.service;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.CopyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public void borrowCopy(Long copyId) {
        changeCopyState(copyId, false);
    }

    public void returnCopy(Long copyId) {
        changeCopyState(copyId, true);
    }

    private void changeCopyState(Long copyId, boolean newState) {
        Optional<Copy> optionalCopy = copyRepository.findById(copyId);

        if (optionalCopy.isEmpty()) {
            throw new IllegalArgumentException(String.format("Exemplaar met id %d bestaat niet.", copyId));
        }

        Copy copy = optionalCopy.get();

        if (copy.getAvailable() == newState) {
            throw new IllegalStateException(String.format("Exemplaar is %s uitgeleend.", newState ? "al" : "niet"));
        }

        copy.setAvailable(newState);
        copyRepository.save(copy);
    }
}
