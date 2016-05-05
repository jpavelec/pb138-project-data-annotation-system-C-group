package cz.muni.pb138.annotationsystem.backend.model;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Subpack {

    private Long id;

    private Pack parent;
    private List<Answer> answers;
    private List<Answer> repeats;
    private List<Grain> noise;

    private List<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pack getParent() {
        return parent;
    }

    public void setParent(Pack parent) {
        this.parent = parent;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getRepeats() {
        return repeats;
    }

    public void setRepeats(List<Answer> repeats) {
        this.repeats = repeats;
    }

    public List<Grain> getNoise() {
        return noise;
    }

    public void setNoise(List<Grain> noise) {
        this.noise = noise;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
