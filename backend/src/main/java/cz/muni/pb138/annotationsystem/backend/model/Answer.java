package cz.muni.pb138.annotationsystem.backend.model;

import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Answer {

    private Long id;

    private Subpack fromSubpack;
    private String answer;
    private Boolean isNoise;

    public Subpack getFromSubpack() {
        return fromSubpack;
    }

    public void setFromSubpack(Subpack fromSubpack) {
        this.fromSubpack = fromSubpack;
    }

    public Boolean isIsNoise() {
        return isNoise;
    }

    public void setIsNoise(Boolean isNoise) {
        this.isNoise = isNoise;
    }

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

    /*public Map<Person, Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(Map<Person, Evaluation> evaluations) {
        this.evaluations = evaluations;
    }*/

    @Override
    public String toString() {
        return "Answer{" + "id=" + id + ", fromSubpack=" + fromSubpack.getName() + ", answer=" + answer + ", isNoise=" + isNoise + '}';
    }
    
    
}
