package cz.muni.pb138.annotationsystem.backend.model;

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
        return "Pack with id "+id+" has question: "+question+", name of pack: "+
                name+", with noiseRate: "+noiseRate + " and repeating: "+repeatingRate;
    }
    
    
}
