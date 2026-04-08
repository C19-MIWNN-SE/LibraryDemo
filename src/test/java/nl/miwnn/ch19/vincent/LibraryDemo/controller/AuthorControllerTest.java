package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Image;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.ImageRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.service.AuthorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private AuthorController authorController;

    @Test
    @DisplayName("saveOrUpdateAuthor should redirect to /author/add when validation fails")
    void saveOrUpdateAuthorShouldRedirectToAddWhenValidationFails() throws Exception {
        Author formAuthor = new Author();
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        MultipartFile imageFile = mock(MultipartFile.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authorController.saveOrUpdateAuthor(
                formAuthor, bindingResult, redirectAttributes, imageFile, false);

        assertThat(result).isEqualTo("redirect:/author/add");
        verify(redirectAttributes).addFlashAttribute(eq("formAuthor"), eq(formAuthor));
    }

    @Test
    @DisplayName("saveOrUpdateAuthor should redirect to /author/all for a new author")
    void saveOrUpdateAuthorShouldRedirectToAllForNewAuthor() throws Exception {
        Author formAuthor = new Author();
        formAuthor.setFirstName("Jan");
        formAuthor.setLastName("Jansen");
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        MultipartFile imageFile = mock(MultipartFile.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(imageFile.isEmpty()).thenReturn(true);

        String result = authorController.saveOrUpdateAuthor(
                formAuthor, bindingResult, redirectAttributes, imageFile, false);

        assertThat(result).isEqualTo("redirect:/author/all");
    }

    @Test
    @DisplayName("saveOrUpdateAuthor should keep existing image when no file uploaded and deleteImage is false")
    void saveOrUpdateAuthorShouldKeepExistingImageWhenNoChange() throws Exception {
        Image existingImage = new Image();
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setFirstName("Jan");
        existingAuthor.setLastName("Jansen");
        existingAuthor.setImage(existingImage);

        Author formAuthor = new Author();
        formAuthor.setId(1L);
        formAuthor.setFirstName("Jan");
        formAuthor.setLastName("Jansen");

        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        MultipartFile imageFile = mock(MultipartFile.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(imageFile.isEmpty()).thenReturn(true);
        when(authorService.findById(1L)).thenReturn(existingAuthor);

        authorController.saveOrUpdateAuthor(
                formAuthor, bindingResult, redirectAttributes, imageFile, false);

        assertThat(existingAuthor.getImage()).isSameAs(existingImage);
    }

    @Test
    @DisplayName("saveOrUpdateAuthor should clear image when deleteImage is true and no file uploaded")
    void saveOrUpdateAuthorShouldClearImageWhenDeleteImageTrue() throws Exception {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setFirstName("Jan");
        existingAuthor.setLastName("Jansen");
        existingAuthor.setImage(new Image());

        Author formAuthor = new Author();
        formAuthor.setId(1L);
        formAuthor.setFirstName("Jan");
        formAuthor.setLastName("Jansen");

        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        MultipartFile imageFile = mock(MultipartFile.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(imageFile.isEmpty()).thenReturn(true);
        when(authorService.findById(1L)).thenReturn(existingAuthor);

        authorController.saveOrUpdateAuthor(
                formAuthor, bindingResult, redirectAttributes, imageFile, true);

        assertThat(existingAuthor.getImage()).isNull();
    }

    @Test
    @DisplayName("saveOrUpdateAuthor should replace image when file is uploaded even if deleteImage is true")
    void saveOrUpdateAuthorShouldReplaceImageWhenFileUploadedEvenIfDeleteImageTrue() throws Exception {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setFirstName("Jan");
        existingAuthor.setLastName("Jansen");
        existingAuthor.setImage(new Image());

        Author formAuthor = new Author();
        formAuthor.setId(1L);
        formAuthor.setFirstName("Jan");
        formAuthor.setLastName("Jansen");

        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        MultipartFile imageFile = mock(MultipartFile.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(imageFile.isEmpty()).thenReturn(false);
        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(imageFile.getContentType()).thenReturn("image/jpeg");
        when(authorService.findById(1L)).thenReturn(existingAuthor);

        authorController.saveOrUpdateAuthor(
                formAuthor, bindingResult, redirectAttributes, imageFile, true);

        assertThat(existingAuthor.getImage()).isNotNull();
        verify(imageRepository).save(any(Image.class));
    }
}