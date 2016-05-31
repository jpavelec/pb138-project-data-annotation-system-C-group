package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PersonDaoImplTest {

    @Inject
    private DataSource dataSource;
    
    @Inject
    private PersonDaoImpl personDao;
    
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
    
    private PersonBuilder sampleJanePersonBuilder() {
        return new PersonBuilder()
                .id(null)
                .username("Jane");
    }
    
    private PersonBuilder sampleJohnPersonBuilder() {
        return new PersonBuilder()
                .id(null)
                .username("John");
    }
    
    @Test
    public void createPerson() throws Exception {
        Person person = sampleJanePersonBuilder().build();
        personDao.create(person);
        Long personId = person.getId();
        assertThat(personId).isNotNull();
        assertThat(personDao.getById(personId))
                .isNotSameAs(person)
                .isEqualToComparingFieldByField(person);
    }
    
    @Test(expected = BeanAlreadyExistsException.class)
    public void createPersonWithDuplicityUsername() throws Exception {
        personDao.create(sampleJanePersonBuilder().build());
        personDao.create(sampleJanePersonBuilder().build());
    }
    
    @Test(expected = ValidationException.class)
    public void createPersonWithNotNullId() throws Exception {
        personDao.create(sampleJanePersonBuilder().id((long) 1).build());
    }
    
    @Test(expected = ValidationException.class)
    public void createPersonWithEmptyUsername() throws Exception {
        personDao.create(sampleJanePersonBuilder().username("").build());
    }
    
    @Test(expected = ValidationException.class)
    public void createPersonWithNullUsername() throws Exception {
        personDao.create(sampleJanePersonBuilder().username(null).build());
    }
    
    @Test(expected = ValidationException.class)
    public void createPersonWithLongUsername() throws Exception {
        personDao.create(sampleJanePersonBuilder().username(
            "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj" +
            "aaaaabbbbbccccc").build());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullPerson() throws Exception {
        personDao.create(null);
    }
    
    @Test
    public void updatePersonUsername() throws Exception {
        Person personForUpdate = sampleJanePersonBuilder().build();
        Person anotherPerson = sampleJohnPersonBuilder().build();
        personDao.create(personForUpdate);
        personDao.create(anotherPerson);
        
        personForUpdate.setUsername("Julia");
        personDao.update(personForUpdate);

        assertThat(personDao.getById(personForUpdate.getId()))
                .isEqualToComparingFieldByField(personForUpdate);
        assertThat(personDao.getById(anotherPerson.getId()))
                .isEqualToComparingFieldByField(anotherPerson);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateNullPerson() throws Exception {
        personDao.update(null);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePersonNullUsername() throws Exception {
        Person personForUpdate = sampleJanePersonBuilder().build();
        personDao.create(personForUpdate);
        personForUpdate.setUsername(null);
        personDao.update(personForUpdate);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void updateNonexistingPerson() throws Exception {
        personDao.update(sampleJohnPersonBuilder().id((long) 25).build());
    }
    
    @Test(expected = ValidationException.class)
    public void updatePersonEmptyUsername() throws Exception {
        Person personForUpdate = sampleJanePersonBuilder().build();
        personDao.create(personForUpdate);
        personForUpdate.setUsername("");
        personDao.update(personForUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePersonNullId() throws Exception {
        Person personForUpdate = sampleJanePersonBuilder().build();
        personDao.create(personForUpdate);
        personForUpdate.setId(null);
        personDao.update(personForUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePersonWithNegativeId() throws Exception {
        Person personForUpdate = sampleJanePersonBuilder().build();
        personDao.create(personForUpdate);
        personForUpdate.setId((long) -1);
        personDao.update(personForUpdate);
    }
    
    @Test(expected = BeanAlreadyExistsException.class)
    public void updateDuplicityUsername() throws Exception {
        Person personForUpdate = sampleJanePersonBuilder().build();
        personDao.create(personForUpdate);
        Person duplicityPerson = sampleJohnPersonBuilder().build();
        personDao.create(duplicityPerson);
        duplicityPerson.setUsername("Jane");
        personDao.update(duplicityPerson);
    }
    
    @Test
    public void getAllPeople() throws Exception {
        assertThat(personDao.getAll()).isEmpty();
        Person personJane = sampleJanePersonBuilder().build();
        Person personJohn = sampleJohnPersonBuilder().build();
        personDao.create(personJane);
        personDao.create(personJohn);
        assertFalse(personDao.getAll().isEmpty());
        assertThat(personDao.getAll())
            .usingFieldByFieldElementComparator()
            .containsOnly(personJane, personJohn);
    }
    
    @Test
    public void getById() throws Exception {
        Person personJane = sampleJanePersonBuilder().build();
        Person personJohn = sampleJohnPersonBuilder().build();
        personDao.create(personJane);
        personDao.create(personJohn);
        assertEquals(personDao.getById(personJane.getId()), personJane);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNegativeId() throws Exception {
        personDao.getById((long) -1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNullId() throws Exception {
        personDao.getById(null);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void getByNonexistingId() throws Exception {
        personDao.getById((long) 25);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteNullPerson() throws Exception {
        personDao.delete(null);
    }
    
    @Test(expected = ValidationException.class)
    public void deletePersonWithNullId() throws Exception {
        personDao.delete(sampleJanePersonBuilder().id(null).build());
    }
    
    @Test
    public void deletePerson() throws Exception {
        Person personJane = sampleJanePersonBuilder().build();
        Person personJohn = sampleJohnPersonBuilder().build();
        personDao.create(personJane);
        personDao.create(personJohn);
        personDao.delete(personJohn);
        assertThat(personDao.getAll())
                .usingFieldByFieldElementComparator()
                .containsOnly(personJane);
    }
    
    

}