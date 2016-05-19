package cz.muni.pb138.annotationsystem.backend.model;


/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Person {

    private Long id;

    private String username;

    public Person() {
    }

    public Person(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", username=" + username + '}';
    }

    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return getId() != null && getId().equals(person.getId());

    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
