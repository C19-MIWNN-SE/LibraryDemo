package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.dto.ChangePasswordDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LibraryUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class LibraryUserControllerTest {

    @Mock
    private LibraryUserService libraryUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LibraryUserController libraryUserController;

    private LibraryUser testUser(String username) {
        return new LibraryUser(username, "$2a$10$encoded", false);
    }

    private ChangePasswordDTO changePasswordDto(String current, String newPw, String check) {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword(current);
        dto.setNewPassword(newPw);
        dto.setCheckPassword(check);
        return dto;
    }

    private BindingResult bindingResultFor(ChangePasswordDTO dto) {
        return new BeanPropertyBindingResult(dto, "changePassword");
    }

    // --- deleteUser ---

    @Test
    @DisplayName("deleteUser should redirect to user overview")
    void deleteUserShouldRedirectToUserOverview() {
        String result = libraryUserController.deleteUser(1L, new RedirectAttributesModelMap());

        assertEquals("redirect:/user/all", result);
    }

    @Test
    @DisplayName("deleteUser should call service deleteById with the given id")
    void deleteUserShouldCallServiceDeleteById() {
        libraryUserController.deleteUser(42L, new RedirectAttributesModelMap());

        verify(libraryUserService, times(1)).deleteById(42L);
    }

    @Test
    @DisplayName("deleteUser should set a success message")
    void deleteUserShouldSetSuccessMessage() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        libraryUserController.deleteUser(1L, redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
    }

    // --- resetPassword ---

    @Test
    @DisplayName("resetPassword should redirect to user overview")
    void resetPasswordShouldRedirectToUserOverview() {
        when(libraryUserService.resetPassword(1L)).thenReturn("abc123defghi");

        String result = libraryUserController.resetPassword(1L, new RedirectAttributesModelMap());

        assertEquals("redirect:/user/all", result);
    }

    @Test
    @DisplayName("resetPassword should include the generated password in the success message")
    void resetPasswordShouldIncludeGeneratedPasswordInSuccessMessage() {
        when(libraryUserService.resetPassword(1L)).thenReturn("abc123defghi");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        libraryUserController.resetPassword(1L, redirectAttributes);

        String message = (String) redirectAttributes.getFlashAttributes().get("successMessage");
        assertNotNull(message);
        assertTrue(message.contains("abc123defghi"));
    }

    // --- showChangePasswordForm ---

    @Test
    @DisplayName("showChangePasswordForm should return the change-password-form view")
    void showChangePasswordFormShouldReturnCorrectView() {
        Model model = mock(Model.class);

        String result = libraryUserController.showChangePasswordForm(model);

        assertEquals("change-password-form", result);
    }

    @Test
    @DisplayName("showChangePasswordForm should add changePassword to the model")
    void showChangePasswordFormShouldAddDtoToModel() {
        Model model = mock(Model.class);

        libraryUserController.showChangePasswordForm(model);

        verify(model).addAttribute(eq("changePassword"), any(ChangePasswordDTO.class));
    }

    // --- changePassword ---

    @Test
    @DisplayName("changePassword should return form when new passwords do not match")
    void changePasswordShouldReturnFormWhenPasswordsDoNotMatch() {
        ChangePasswordDTO dto = changePasswordDto("huidig", "nieuw1", "nieuw2");

        String result = libraryUserController.changePassword(
                dto, bindingResultFor(dto), testUser("testgebruiker"), new RedirectAttributesModelMap());

        assertEquals("change-password-form", result);
    }

    @Test
    @DisplayName("changePassword should add a binding error when new passwords do not match")
    void changePasswordShouldAddBindingErrorWhenPasswordsDoNotMatch() {
        ChangePasswordDTO dto = changePasswordDto("huidig", "nieuw1", "nieuw2");
        BindingResult bindingResult = bindingResultFor(dto);

        libraryUserController.changePassword(
                dto, bindingResult, testUser("testgebruiker"), new RedirectAttributesModelMap());

        assertTrue(bindingResult.hasErrors());
        assertNotNull(bindingResult.getFieldError("checkPassword"));
    }

    @Test
    @DisplayName("changePassword should return form when current password is wrong")
    void changePasswordShouldReturnFormWhenCurrentPasswordIsWrong() {
        ChangePasswordDTO dto = changePasswordDto("fout", "nieuw", "nieuw");
        when(libraryUserService.changePassword("testgebruiker", dto)).thenReturn(false);

        String result = libraryUserController.changePassword(
                dto, bindingResultFor(dto), testUser("testgebruiker"), new RedirectAttributesModelMap());

        assertEquals("change-password-form", result);
    }

    @Test
    @DisplayName("changePassword should add a binding error when current password is wrong")
    void changePasswordShouldAddBindingErrorWhenCurrentPasswordIsWrong() {
        ChangePasswordDTO dto = changePasswordDto("fout", "nieuw", "nieuw");
        when(libraryUserService.changePassword("testgebruiker", dto)).thenReturn(false);
        BindingResult bindingResult = bindingResultFor(dto);

        libraryUserController.changePassword(
                dto, bindingResult, testUser("testgebruiker"), new RedirectAttributesModelMap());

        assertTrue(bindingResult.hasErrors());
        assertNotNull(bindingResult.getFieldError("currentPassword"));
    }

    @Test
    @DisplayName("changePassword should redirect to book overview on success")
    void changePasswordShouldRedirectToBookOverviewOnSuccess() {
        ChangePasswordDTO dto = changePasswordDto("juist", "nieuw", "nieuw");
        when(libraryUserService.changePassword("testgebruiker", dto)).thenReturn(true);

        String result = libraryUserController.changePassword(
                dto, bindingResultFor(dto), testUser("testgebruiker"), new RedirectAttributesModelMap());

        assertEquals("redirect:/book/all", result);
    }

    @Test
    @DisplayName("changePassword should set a success message on success")
    void changePasswordShouldSetSuccessMessageOnSuccess() {
        ChangePasswordDTO dto = changePasswordDto("juist", "nieuw", "nieuw");
        when(libraryUserService.changePassword("testgebruiker", dto)).thenReturn(true);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        libraryUserController.changePassword(
                dto, bindingResultFor(dto), testUser("testgebruiker"), redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
    }
}