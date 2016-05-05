package cz.muni.pb138.annotationsystem.backend.model;

import java.util.List;
import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Answer {

    private Long id;

    private String answer;
    private Map<User, Evaluation> evaluations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Map<User, Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(Map<User, Evaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
