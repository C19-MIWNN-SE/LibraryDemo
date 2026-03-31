package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Arjan Loermans
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
