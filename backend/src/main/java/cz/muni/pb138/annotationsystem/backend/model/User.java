package cz.muni.pb138.annotationsystem.backend.model;

import java.util.List;
import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class User {

    private Long id;

    private String username;
    private boolean admin;

    Map<Answer, Evaluation> evals;

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Map<Answer, Evaluation> getEvals() {
        return evals;
    }

    public void setEvals(Map<Answer, Evaluation> evals) {
        this.evals = evals;
    }
}
