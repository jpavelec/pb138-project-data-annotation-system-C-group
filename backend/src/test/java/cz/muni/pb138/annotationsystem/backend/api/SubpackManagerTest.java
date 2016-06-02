package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.sql.DataSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class SubpackManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private SubpackManager subpackManager;

    @Inject
    private PackManager packManager;

    @Inject
    private PersonManager personManager;

    @Inject
    private AnswerManager answerManager;

    @Before
    public void setUp() throws Exception {
        Resource create = new ClassPathResource("createTables.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), create);
    }

    @After
    public void tearDown() throws Exception {
        Resource drop = new ClassPathResource("dropTables.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), drop);
    }


    @Test
    public void getSubpackById() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        assertNotNull(subpacks.get(0));

        Subpack result = subpackManager.getSubpackById(subpacks.get(0).getId());

        assertNotNull(result);
        assertEquals(subpacks.get(0), result);

        assertEquals(subpacks.get(0).getId(), result.getId());
        assertEquals(subpacks.get(0).getName(), result.getName());
        assertEquals(subpacks.get(0).getParent(), result.getParent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSubpackByIdNullId() throws Exception {
        subpackManager.getSubpackById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSubpackByIdNegativeId() throws Exception {
        subpackManager.getSubpackById((long) -1);
    }

    @Test(expected = BeanNotExistsException.class)
    public void getSubpackByIdUnknownId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        subpackManager.getSubpackById((long) 99);
    }



    @Test
    public void getSubpacksInPack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        List<String> packAnswers = TestUtils.getAnswers1();
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        assertNotNull(subpacks.get(0));

        assertTrue(subpacks.size() == Math.ceil(packAnswers.size() / ((double)TestUtils.SUBPACK_SIZE_1)));

        for (Subpack subpack : subpacks) {
            assertNotNull(subpack);

            assertEquals(packs[1], subpack.getParent());

            List<Answer> answers = answerManager.getAnswersInSubpack(subpack);

            for (Answer answer : answers) {
                assertNotNull(answer);

                packAnswers.contains(answer.getAnswer());
            }
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void getSubpacksInPackNullPack() throws Exception {
        subpackManager.getSubpacksInPack(null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getSubpacksInPackUnknownPack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Pack pack = TestUtils.getPack0();
        pack.setId((long) 99);
        subpackManager.getSubpacksInPack(pack);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getSubpacksInPackWithoutId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        subpackManager.getSubpacksInPack(TestUtils.getPack0());
    }



    @Test
    public void getSubpacksAssignedToPerson() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        List<Subpack> subpacks0 = subpackManager.getSubpacksInPack(packs[0]);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        subpacks.add(subpacks0.get(1));
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        List<Subpack> result = subpackManager.getSubpacksAssignedToPerson(persons[1]);
        assertEquals(new HashSet<>(subpacks), new HashSet<>(result));
    }
    @Test
    public void getSubpacksAssignedToPersonEmpty() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        List<Subpack> subpacks = subpackManager.getSubpacksAssignedToPerson(persons[1]);

        assertEquals(new ArrayList<Subpack>(), subpacks);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getSubpacksAssignedToPersonNullPerson() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.getSubpacksAssignedToPerson(null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getSubpacksAssignedToPersonUnknownPerson() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        Person person = TestUtils.getPerson1();
        person.setId((long) 99);
        subpackManager.getSubpacksAssignedToPerson(person);
    }
    @Test(expected = ValidationException.class)
    public void getSubpacksAssignedToPersonWithoutPersonId() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        Person person = TestUtils.getPerson1();
        subpackManager.getSubpacksAssignedToPerson(person);
    }



    @Test
    public void getPersonsAssignedToSubpack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);
        List<Person> persons = new ArrayList<>();
        persons.add(personsAll[0]);
        persons.add(personsAll[1]);

        subpackManager.updateSubpacksAssignment(subpacks.get(1), persons);

        List<Person> assignedPersons = subpackManager.getPersonsAssignedToSubpack(subpacks.get(1));
        assertEquals(new HashSet<>(persons), new HashSet<>(assignedPersons));
    }
    @Test(expected = IllegalArgumentException.class)
    public void getPersonsAssignedToSubpackNullSubpack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);

        subpackManager.getPersonsAssignedToSubpack(null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getPersonsAssignedToSubpackUnknownSubpack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);
        List<Person> persons = new ArrayList<>();
        persons.add(personsAll[0]);
        persons.add(personsAll[1]);

        subpackManager.updateSubpacksAssignment(subpacks.get(1), persons);

        subpacks.get(0).setId((long) 99);

        subpackManager.getPersonsAssignedToSubpack(subpacks.get(0));
    }



    @Test
    public void updatePersonsAssignment() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        List<Subpack> subpacks0 = subpackManager.getSubpacksInPack(packs[0]);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        subpacks.add(subpacks0.get(1));

        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        List<Subpack> result = subpackManager.getSubpacksAssignedToPerson(persons[1]);
        assertEquals(new HashSet<>(subpacks), new HashSet<>(result));

        for (Subpack subpack : subpacks) {
            List<Person> assignedPersons = subpackManager.getPersonsAssignedToSubpack(subpack);
            assertTrue(assignedPersons.contains(persons[1]));
        }
    }
    @Test
    public void updatePersonsAssignmentReassigment() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        List<Subpack> subpacks0 = subpackManager.getSubpacksInPack(packs[0]);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        subpacks.add(subpacks0.get(1));

        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        subpacks0.add(subpacks.get(1));

        subpackManager.updatePersonsAssignment(persons[1], subpacks0);

        List<Subpack> result = subpackManager.getSubpacksAssignedToPerson(persons[1]);
        assertEquals(new HashSet<>(subpacks0), new HashSet<>(result));

        for (Subpack subpack : subpacks0) {
            List<Person> assignedPersons = subpackManager.getPersonsAssignedToSubpack(subpack);
            assertTrue(assignedPersons.contains(persons[1]));
        }
    }
    @Test
    public void updatePersonsAssignmentEmpty() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        subpackManager.updatePersonsAssignment(persons[1], new ArrayList<Subpack>());

        List<Subpack> result = subpackManager.getSubpacksAssignedToPerson(persons[1]);
        assertEquals(new ArrayList<Subpack>(), result);

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);
        subpacks.addAll(subpackManager.getSubpacksInPack(packs[1]));
        for (Subpack subpack : subpacks) {
            List<Person> assignedPersons = subpackManager.getPersonsAssignedToSubpack(subpack);
            assertFalse(assignedPersons.contains(persons[1]));
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void updatePersonsAssignmentNullPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        List<Subpack> subpacks0 = subpackManager.getSubpacksInPack(packs[0]);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        subpacks.add(subpacks0.get(1));

        subpackManager.updatePersonsAssignment(null, subpacks);
    }
    @Test(expected = IllegalArgumentException.class)
    public void updatePersonsAssignmentNullSubpacks() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);

        subpackManager.updatePersonsAssignment(persons[1], null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void updatePersonsAssignmentUnknownPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] persons = TestUtils.createPersons(personManager);
        Person person = TestUtils.getPerson1();
        person.setId((long) 99);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);

        subpackManager.updatePersonsAssignment(person, subpacks);
    }




    @Test
    public void updateSubpacksAssignment() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);
        List<Person> persons = new ArrayList<>();
        persons.add(personsAll[0]);
        persons.add(personsAll[1]);

        subpackManager.updateSubpacksAssignment(subpacks.get(1), persons);

        List<Person> assignedPersons = subpackManager.getPersonsAssignedToSubpack(subpacks.get(1));
        assertEquals(new HashSet<>(persons), new HashSet<>(assignedPersons));

        for (Person person : persons) {
            List<Subpack> assignedSubpacks = subpackManager.getSubpacksAssignedToPerson(person);
            assertTrue(assignedSubpacks.contains(subpacks.get(1)));
        }
    }
    @Test
    public void updateSubpacksAssignmentEmpty() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);
        List<Person> persons = new ArrayList<>();
        persons.add(personsAll[0]);
        persons.add(personsAll[1]);

        subpackManager.updateSubpacksAssignment(subpacks.get(1), new ArrayList<Person>());

        List<Person> assignedPersons = subpackManager.getPersonsAssignedToSubpack(subpacks.get(1));
        assertEquals(new ArrayList<>(), assignedPersons);

        for (Person person : personsAll) {
            List<Subpack> assignedSubpacks = subpackManager.getSubpacksAssignedToPerson(person);
            assertFalse(assignedSubpacks.contains(subpacks.get(1)));
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void updateSubpacksAssignmentNullSubpack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);
        List<Person> persons = Arrays.asList(personsAll);

        subpackManager.updateSubpacksAssignment(null, persons);
    }
    @Test(expected = IllegalArgumentException.class)
    public void updateSubpacksAssignmentNullPersons() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);

        subpackManager.updateSubpacksAssignment(subpacks.get(0), null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void updateSubpacksAssignmentUnknownSubpack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        Person[] personsAll = TestUtils.createPersons(personManager);
        List<Person> persons = Arrays.asList(personsAll);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[0]);
        subpacks.get(0).setId((long) 99);
        subpackManager.updateSubpacksAssignment(subpacks.get(0), persons);
    }





}