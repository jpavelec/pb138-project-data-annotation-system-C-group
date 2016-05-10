package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface SubpackManager {

    Subpack getSubpackById(Long id);

    List<Subpack> getSubpacksInPack(Pack pack);

    List<Subpack> getSubpacksAssignedToPerson(Person person);

    List<Person> getPersonsAssignedToSubpack(Subpack subpack);


    void updatePersonsAssignment(Person person, List<Subpack> subpacks);

    void updateSubpacksAssignment(Subpack subpack, List<Person> persons);

}
