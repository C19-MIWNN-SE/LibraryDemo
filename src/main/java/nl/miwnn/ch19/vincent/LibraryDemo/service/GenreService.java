package nl.miwnn.ch19.vincent.LibraryDemo.service;

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Genre;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Velthuizen
 * Handle business logic for Genres
 */
@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    public GenreService(GenreRepository genreRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Genre met id %d niet gevonden", id)));
    }

    public void saveGenre(Genre genre) {
        genreRepository.save(genre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = findById(id);
        for (Book book : new ArrayList<>(genre.getBooks())) {
            book.setGenre(null);
            bookRepository.save(book);
        }
        genreRepository.delete(genre);
    }

    public Genre findByShortName(String shortName) {
        return genreRepository.findByShortNameIgnoreCase(shortName).orElseThrow(
                () -> new EntityNotFoundException(String.format("Genre met shortname %s niet gevonden", shortName)));
    }

}
