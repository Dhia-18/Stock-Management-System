package main.Entities;

public class Admin {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarPath;
    
    public Admin(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Admin(String username, String password, String firstName, String lastName, String email){
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Admin(String username, String password, String firstName, String lastName, String email, String avatarPath){
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarPath = avatarPath;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarPath(){
        return(avatarPath);
    }

    public void setAvatarPath(String path){
        avatarPath = path;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

