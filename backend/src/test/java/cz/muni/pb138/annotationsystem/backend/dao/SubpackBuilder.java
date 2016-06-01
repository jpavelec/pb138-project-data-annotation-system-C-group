package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.List;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class SubpackBuilder {
    
    private Long id;

    private Pack parent;
    private String name;
    
    public SubpackBuilder id(Long id) {
        this.id = id;
        return this;
    }
    
    public SubpackBuilder parent(Pack parent) {
        this.parent = parent;
        return this;
    }
    
    public SubpackBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public Subpack build() {
        Subpack subpack = new Subpack();
        subpack.setId(id);
        subpack.setParent(parent);
        subpack.setName(name);
        return subpack;
    }

}
