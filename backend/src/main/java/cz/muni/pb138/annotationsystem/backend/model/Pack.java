package cz.muni.pb138.annotationsystem.backend.model;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Pack {

    private Long id;

    private String question;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int noiseRate;
    private int repeating;

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

    public int getNoiseRate() {
        return noiseRate;
    }

    public void setNoiseRate(int noiseRate) {
        this.noiseRate = noiseRate;
    }

    public int getRepeating() {
        return repeating;
    }

    public void setRepeating(int repeating) {
        this.repeating = repeating;
    }

    @Override
    public String toString() {
        return "Pack with id "+id+" has question: "+question+", name of pack: "+
                name+", with noiseRate: "+noiseRate + " and repeating: "+repeating;
    }
    
    
}
