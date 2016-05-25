package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Person;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PersonManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private PersonManager personManager;

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
    public void createPerson() throws Exception {

        Person person0 = TestUtils.getPerson0();
        personManager.createPerson(person0);

        assertNotNull(person0.getId());

        assertTrue(personManager.getAllPersons().size() == 1);

        assertEquals(person0, personManager.getPersonById(person0.getId()));

        assertEquals(person0, personManager.getPersonByUsername(person0.getUsername()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPersonWithoutPerson() throws Exception {
        personManager.createPerson(null);
    }

    @Test(expected = ValidationException.class)
    public void createPersonWithId() throws Exception {
        Person person0 = TestUtils.getPerson0();
        person0.setId((long) 1);
        personManager.createPerson(person0);
    }

    @Test(expected = ValidationException.class)
    public void createPersonWithoutUsername() throws Exception {
        Person person0 = TestUtils.getPerson0();
        person0.setUsername(null);
        personManager.createPerson(person0);
    }

    @Test(expected = ValidationException.class)
    public void createPersonWithEmptyUsername() throws Exception {
        Person person0 = TestUtils.getPerson0();
        person0.setUsername("");
        personManager.createPerson(person0);
    }

    @Test(expected = BeanAlreadyExistsException.class)
    public void createPersonWithDuplicitUsername() throws Exception {
        Person person0 = TestUtils.getPerson0();
        personManager.createPerson(person0);

        Person person1 = TestUtils.getPerson0();
        personManager.createPerson(person1);
    }



    @Test
    public void getPersonByUsername() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);

        Person result = personManager.getPersonByUsername(persons[1].getUsername());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(persons[1].getId(), result.getId());
        assertEquals(persons[1].getUsername(), result.getUsername());

        assertEquals(persons[1], result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPersonByUsernameWithoutUsername() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        personManager.getPersonByUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPersonByUsernameWithEmptyUsername() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        personManager.getPersonByUsername("");
    }

    @Test(expected = BeanNotExistsException.class)
    public void getPersonByUsernameUnknownUsername() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        personManager.getPersonByUsername("UNKNOWN_USERNAME");
    }



    @Test
    public void getPersonById() throws Exception {

        Person[] persons = TestUtils.createPersons(personManager);

        Person result = personManager.getPersonById(persons[1].getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(persons[1].getId(), result.getId());
        assertEquals(persons[1].getUsername(), result.getUsername());

        assertEquals(persons[1], result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPersonByIdWithoutId() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        personManager.getPersonById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPersonByIdWithNegativeId() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        personManager.getPersonById((long) -1);
    }

    @Test(expected = BeanNotExistsException.class)
    public void getPersonByIdUnknownId() throws Exception {
        Person[] persons = TestUtils.createPersons(personManager);
        personManager.getPersonById((long) 99);
    }



    @Test
    public void getAllPersons() throws Exception {
        Set<Person> persons = new HashSet<>(Arrays.asList(TestUtils.createPersons(personManager)));

        Set<Person> result = new HashSet<>(personManager.getAllPersons());

        assertEquals(persons, result);
    }

    @Test
    public void getAllPersonsEmpty() throws Exception {
        List<Person> result = personManager.getAllPersons();

        assertEquals(new ArrayList<Person>(), result);
    }

}