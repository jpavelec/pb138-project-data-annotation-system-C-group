package cz.muni.pb138.annotationsystem.backend.model;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Grain {

    private Long id;

    private Answer answer;
    private Evaluation evaluation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
