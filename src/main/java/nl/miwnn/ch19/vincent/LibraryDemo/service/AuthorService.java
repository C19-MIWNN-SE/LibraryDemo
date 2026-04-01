package nl.miwnn.ch19.vincent.LibraryDemo.service;

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Vincent Velthuizen
 * Handle business logic for Authors
 */
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Auteur met id %d niet gevonden", id)));
    }

    public Author findByLastNameAndFirstName(String lastName, String firstName) {
        return authorRepository.findByLastNameAndFirstName(lastName, firstName).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Auteur %s %s niet gevonden", firstName, lastName)));
    }

    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }
}
