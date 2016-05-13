package cz.muni.pb138.annotationsystem.backend.model;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Answer {

    private Long id;

    private Subpack fromSubpack;
    private String answer;
    private Boolean isNoise;

    public Answer() {
    }

    public Answer(Subpack fromSubpack, String answer, Boolean isNoise) {
        this.fromSubpack = fromSubpack;
        this.answer = answer;
        this.isNoise = isNoise;
    }

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

    
    @Override
    public String toString() {
        return "Answer{" + "id=" + id + ", fromSubpack=" + fromSubpack.getName() + 
               ", answer=" + answer + ", isNoise=" + isNoise + '}';
    }
    
    
}
