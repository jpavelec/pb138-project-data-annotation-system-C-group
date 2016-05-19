package cz.muni.pb138.annotationsystem.backend.model;

import java.util.Objects;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Pack {

    private Long id;

    private String question;
    private String name;
    private double noiseRate;
    private double repeatingRate;

    public Pack() {
    }

    public Pack(String question, String name, double noiseRate, double repeatingRate) {
        this.question = question;
        this.name = name;
        this.noiseRate = noiseRate;
        this.repeatingRate = repeatingRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public double getNoiseRate() {
        return noiseRate;
    }

    public void setNoiseRate(double noiseRate) {
        this.noiseRate = noiseRate;
    }

    public double getRepeatingRate() {
        return repeatingRate;
    }

    public void setRepeatingRate(double repeatingRate) {
        this.repeatingRate = repeatingRate;
    }

    @Override
    public String toString() {
        return "Pack{" + "id=" + id + ", question=" + question + ", name=" + name + 
                ", noiseRate=" + noiseRate + ", repeatingRate=" + repeatingRate + '}';
    }
    
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pack packObj = (Pack) o;

        return getId() != null && getId().equals(packObj.getId());
    }
    
        
    
    
}
