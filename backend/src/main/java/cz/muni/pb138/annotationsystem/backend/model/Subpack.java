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
    private List<Person> users;

    public Subpack() {
    }

    public Subpack(Pack parent, String name) {
        this(parent, name, new ArrayList<Person>());
    }

    public Subpack(Pack parent, String name, List<Person> users) {
        this.parent = parent;
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

    public List<Person> getUsers() {
        return users;
    }

    public void setUsers(List<Person> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        /*return "Subpack with id "+id+" has parent pack "+parent+
               " subpack name is "+name+" contains "+answers.size()+
               " answers (noise is included) and "+repeats.size()+
               " repeated answers for "+users.size()+" users";*/
        
        return "Subpack with id "+id+" has parent pack "+parent+
               " subpack name is "+name+" contains 0"+
               " answers (noise is included) and 0"+
               " repeated answers for 0 users";
    }
    
    
}
