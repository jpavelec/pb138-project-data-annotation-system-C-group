package cz.muni.pb138.annotationsystem.backend.model;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Pack {

    private Long id;

    private String question;

    private List<Grain> noise;
    private Double noiseRate;
    private Double repeating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Grain> getNoise() {
        return noise;
    }

    public void setNoise(List<Grain> noise) {
        this.noise = noise;
    }

    public Double getNoiseRate() {
        return noiseRate;
    }

    public void setNoiseRate(Double noiseRate) {
        this.noiseRate = noiseRate;
    }

    public Double getRepeating() {
        return repeating;
    }

    public void setRepeating(Double repeating) {
        this.repeating = repeating;
    }
}
