package nl.miwnn.ch19.vincent.LibraryDemo.dto;

/**
 * @author Vincent Velthuizen
 * Supports change password form for logged-in users
 */
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String checkPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }
}