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
        int countOfUsers;
        if (users == null) {
            countOfUsers = 0;
        } else {
            countOfUsers = users.size();
        }
        return "Subpack{" + "id=" + id + ", name=" + name +", from pack=" + 
                parent.getName()  + ", annotates " + countOfUsers + " users}";
    }
    
    
    
    
}
