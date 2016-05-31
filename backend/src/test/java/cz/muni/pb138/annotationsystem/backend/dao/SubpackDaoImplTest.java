package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.derby.iapi.services.io.ArrayUtil;
import static org.assertj.core.api.Assertions.assertThat;
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

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class SubpackDaoImplTest {

    @Inject
    private DataSource dataSource;
    
    @Inject
    private PackDaoImpl packDao;
    
    @Inject
    private PersonDaoImpl personDao;
    
    @Inject
    private SubpackDaoImpl subpackDao;

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
    
    private PackBuilder sampleAnimalPack() {
        return new PackBuilder()
                .id(null)
                .question("Is it an animal?")
                .name("Animal")
                .noiseRate(3.23)
                .repeatingRate(2.94);
    }
    
    private PackBuilder sampleThingPack() {
        return new PackBuilder()
                .id(null)
                .question("Is it a thing?")
                .name("Thing")
                .noiseRate(10.2)
                .repeatingRate(15.06);
    }
    
    private PersonBuilder sampleGeorgePerson() {
        return new PersonBuilder()
                .id(null)
                .username("George");
    }
    
    private PersonBuilder sampleAnastaciaPerson() {
        return new PersonBuilder()
                .id(null)
                .username("Anastacia");
    }
    
    private SubpackBuilder sampleAnimalSubpackWithUsersGeorgeAndAnastacia() throws DaoException {
        Person userGeorge = sampleGeorgePerson().build();
        personDao.create(userGeorge);
        Person userAnastacia = sampleAnastaciaPerson().build();
        personDao.create(userAnastacia);
        List<Person> users = new ArrayList<>();
        users.add(userGeorge);
        users.add(userAnastacia);
        Pack packAnimal = sampleAnimalPack().build();
        packDao.create(packAnimal);
        return new SubpackBuilder()
                .id(null)
                .name("Animal_01")
                .parent(packAnimal)
                .users(users);
    }
    
    private SubpackBuilder sampleThingSubpackWithNoUser() throws DaoException {
        List<Person> users = new ArrayList<>();
        Pack packThing = sampleThingPack().build();
        packDao.create(packThing);
        return new SubpackBuilder()
                .id(null)
                .name("Thing_01")
                .parent(packThing)
                .users(users);
    }
        
    @Test
    public void createSubpack() throws Exception {
        Subpack subpack = sampleAnimalSubpackWithUsersGeorgeAndAnastacia().build();
        subpackDao.create(subpack);
        Long subpackId = subpack.getId();
        assertThat(subpackId).isNotNull();
        assertThat(subpackDao.getById(subpackId))
                .isNotSameAs(subpack)
                .isEqualToComparingFieldByField(subpack);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullSubpack() throws Exception {
        subpackDao.create(null);
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithExistingId() throws Exception {
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia().id((long) 1).build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithNullParent() throws Exception {
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia().parent(null).build());
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void createSubpackWithNonExistingParent() throws Exception {
        Pack packNotInDb = sampleAnimalPack().id((long) 25).build();
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia().parent(packNotInDb).build());
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void createSubpackWithNonExistingUser() throws Exception {
        Person personNotInDb = sampleAnastaciaPerson().id((long) 25).build();
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia().users(Arrays.asList(personNotInDb)).build());
    }

    @Test(expected = ValidationException.class)
    public void createSubpackWithNullListOfUsers() throws Exception {
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia()
                            .users(null)
                            .build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithLongName() throws Exception {
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia()
                            .name("aaaaabbbbbcccccdddddeeeeefffffg")
                            .build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithEmptyName() throws Exception {
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia()
                            .name("")
                            .build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithNullName() throws Exception {
        subpackDao.create(sampleAnimalSubpackWithUsersGeorgeAndAnastacia()
                            .name(null)
                            .build());
    }
    
    @Test
    public void getById() throws Exception {
        Subpack subpack = sampleAnimalSubpackWithUsersGeorgeAndAnastacia().build();
        subpackDao.create(subpack);
        assertThat(subpackDao.getById(subpack.getId()))
                .isNotNull()
                .isEqualToComparingFieldByField(subpack);
    }
    
    @Test
    public void getAll() throws Exception {
        Subpack subpackAnimal = sampleAnimalSubpackWithUsersGeorgeAndAnastacia().build();
        Subpack subpackThing = sampleThingSubpackWithNoUser().build();
        subpackDao.create(subpackAnimal);
        subpackDao.create(subpackThing);
        assertThat(subpackDao.getAll())
                .usingFieldByFieldElementComparator()
                .containsOnly(subpackAnimal, subpackThing);
    }
    
    /*@Test
    public void updateSubpack() throws Exception {
        su
    }*/
    
    
}