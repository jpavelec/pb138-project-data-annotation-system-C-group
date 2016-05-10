package cz.muni.pb138.annotationsystem.backend.model;

import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Person {

    private Long id;

    private String username;
    private boolean isAdmin;

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

    /*public Map<Answer, Evaluation> getEvals() {
        return evals;
    }

    public void setEvals(Map<Answer, Evaluation> evals) {
        this.evals = evals;
    }*/

    @Override
    public String toString() {
        if (this.getIsAdmin())
            return "Person with id " + id + " is an admin and has username " + username;
        return "Person with id " + id + " is a user and has username " + username;
    }
    
}
