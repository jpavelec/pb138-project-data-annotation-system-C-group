package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Person;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class PersonBuilder {
    
    private Long id;
    private String username;
    
    public PersonBuilder id(Long id) {
        this.id = id;
        return this;
    }
    
    public PersonBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    public Person build() {
        Person person = new Person();
        person.setId(id);
        person.setUsername(username);
        return person;
    }
    

}
