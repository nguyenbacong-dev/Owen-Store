package model;

public class Account {
    private String fullname;
    private String email;
    private String username;
    private String password;
    private String role; // "admin" hoặc "user"

    public Account() {
    }

    public Account(String fullname, String email, String username, String password, String role) {
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter và Setter
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
