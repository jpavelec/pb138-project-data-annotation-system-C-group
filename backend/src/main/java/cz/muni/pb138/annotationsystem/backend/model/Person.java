package cz.muni.pb138.annotationsystem.backend.model;


/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Person {

    private Long id;

    private String username;
    private boolean isAdmin;

    public Person() {
    }

    public Person(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    @Override
    public String toString() {
        if (this.getIsAdmin())
            return "Person with id " + id + " is an admin and has username " + username;
        return "Person with id " + id + " is a user and has username " + username;
    }
    
}
