package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Pack;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class PackBuilder {
    
    private Long id;

    private String question;
    private String name;
    private double noiseRate;
    private double repeatingRate;
    
    public PackBuilder id(Long id) {
        this.id = id;
        return this;
    }
    
    public PackBuilder question(String question) {
        this.question = question;
        return this;
    }
    
    public PackBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public PackBuilder noiseRate(double noiseRate) {
        this.noiseRate = noiseRate;
        return this;
    }
    
    public PackBuilder repeatingRate(double repeatingRate) {
        this.repeatingRate = repeatingRate;
        return this;
                
    }
    
    public Pack build() {
        Pack pack = new Pack();
        pack.setId(id);
        pack.setName(name);
        pack.setQuestion(question);
        pack.setNoiseRate(noiseRate);
        pack.setRepeatingRate(repeatingRate);
        return pack;
    }

}
