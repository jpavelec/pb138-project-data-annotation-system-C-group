package cz.muni.pb138.annotationsystem.backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Subpack {

    private Long id;

    private Pack parent;
    private String name;
    
    // TO DO delete
    private List<Person> users;

    public Subpack() {
    }

    public Subpack(Pack parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Subpack(Pack parent, String name, List<Person> users) {
        this.parent = parent;
        this.name = name;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pack getParent() {
        return parent;
    }

    public void setParent(Pack parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // TO DO delete
    public List<Person> getUsers() {
        return null;
    }

    // TO DO delete
    public void setUsers(List<Person> users) {
        
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subpack subpackObj = (Subpack) o;

        return getId() != null && getId().equals(subpackObj.getId());
    }
}
