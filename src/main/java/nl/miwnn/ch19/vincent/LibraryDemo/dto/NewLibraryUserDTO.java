package nl.miwnn.ch19.vincent.LibraryDemo.dto;

/**
 * @author Vincent Velthuizen
 * Supports new user form
 */
public class NewLibraryUserDTO {
    private String username;
    private String plainPassword;
    private String checkPassword;
    private boolean administrator;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
}
