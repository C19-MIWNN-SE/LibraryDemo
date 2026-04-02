package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.service.AuthorService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Vincent Velthuizen
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("showBookOverview_should return 200 and books in model")
    void showBookOverviewShouldReturn200AndBooksInModel() throws Exception {
        List<Book> books = List.of(
                new Book("De Aanslag", 1982),
                new Book("Max Havelaar", 1860)
        );
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/book/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-overview"))
                .andExpect(model().attributeExists("allBooks"));
    }
}