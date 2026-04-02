package nl.miwnn.ch19.vincent.LibraryDemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Vincent Velthuizen
 */
@SpringBootTest
@ActiveProfiles("test")
public class LibraryDemoApplicationTest {

    @Test
    @DisplayName("Does it work or does it smoke")
    void doesItWorkOrDoesItSmoke() {}
}
