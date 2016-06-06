package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.util.Arrays;
import javax.inject.Inject;
import javax.sql.DataSource;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    private AnswerDaoImpl answerDao;
    
    @Inject
    private SubpackDaoImpl subpackDao;

    @Before
    public void setUp() throws Exception {
        Resource create = new ClassPathResource("createTables.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), create);
        prepareTestData();
    }

    @After
    public void tearDown() throws Exception {
        Resource drop = new ClassPathResource("dropTables.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), drop);
    }
    
    private Pack animalPack, thingPack, packNotInDB, packWithNullId;
    private Person pepePerson, leonPerson, personNotInDB, personWithNullId;
    
    private void prepareTestData() throws Exception {
        animalPack = new PackBuilder().question("an animal").name("animal").noiseRate(12.25).repeatingRate(5.5).build();
        thingPack = new PackBuilder().question("a thing").name("thing").noiseRate(0).repeatingRate(0).build();
        packNotInDB = new PackBuilder().id((long) 100).build();
        packWithNullId = new PackBuilder().id(null).build();
        packDao.create(animalPack);
        packDao.create(thingPack);
        
        pepePerson = new PersonBuilder().username("Pepe").build();
        leonPerson = new PersonBuilder().username("Leon").build();
        personDao.create(pepePerson);
        personDao.create(leonPerson);
        personNotInDB = new PersonBuilder().id((long) 100).build();
        personWithNullId = new PersonBuilder().id(null).build();
        
        try {
            if (personDao.getById(personNotInDB.getId()) != null ||
                packDao.getById(packNotInDB.getId()) != null) {
                throw new BeanAlreadyExistsException("Test entity which shouldnt exist is in DB!");
            }
        } catch (BeanNotExistsException ex) {}
        
        
        
    }
    
    private SubpackBuilder sampleAnimal01Subpack() throws DaoException {
        return new SubpackBuilder()
                .id(null)
                .name("Animal_01")
                .parent(animalPack);
    }
    
    private SubpackBuilder sampleAnimal02Subpack() throws DaoException {
        return new SubpackBuilder()
                .id(null)
                .name("Animal_02")
                .parent(animalPack);
    }
    
    private SubpackBuilder sampleThingSubpack() throws DaoException {
        return new SubpackBuilder()
                .id(null)
                .name("Thing_01")
                .parent(thingPack);
    }
        
    @Test
    public void createSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
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
        subpackDao.create(sampleAnimal01Subpack().id((long) 1).build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithNullParent() throws Exception {
        subpackDao.create(sampleAnimal01Subpack().parent(null).build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithParentPackWithNullID() throws Exception {
        subpackDao.create(sampleAnimal01Subpack().parent(packWithNullId).build());
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void createSubpackWithNonExistingParent() throws Exception {
        subpackDao.create(sampleAnimal01Subpack().parent(packNotInDB).build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithLongName() throws Exception {
        subpackDao.create(sampleAnimal01Subpack()
                            .name("aaaaabbbbbcccccdddddeeeeefffffaaaaabbbbbcccccdddddeeeeefffffg")
                            .build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithEmptyName() throws Exception {
        subpackDao.create(sampleAnimal01Subpack()
                            .name("")
                            .build());
    }
    
    @Test(expected = ValidationException.class)
    public void createSubpackWithNullName() throws Exception {
        subpackDao.create(sampleAnimal01Subpack()
                            .name(null)
                            .build());
    }
    
    @Test
    public void updateSubpack() throws Exception {
        Subpack subpackForUpdate = sampleAnimal01Subpack().build();
        Subpack anotherSubpack = sampleAnimal02Subpack().build();
        subpackDao.create(subpackForUpdate);
        subpackDao.create(anotherSubpack);
        
        subpackForUpdate.setName("Animal_001");
        subpackDao.update(subpackForUpdate);

        assertThat(subpackDao.getById(subpackForUpdate.getId()))
                .isEqualToComparingFieldByField(subpackForUpdate);
        assertThat(subpackDao.getById(anotherSubpack.getId()))
                .isEqualToComparingFieldByField(anotherSubpack);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateNullSubpack() throws Exception {
        subpackDao.update(null);
    }
    
    @Test(expected = ValidationException.class)
    public void updateSubpackNullName() throws Exception {
        Subpack subpackForUpdate = sampleAnimal01Subpack().build();
        subpackDao.create(subpackForUpdate);
        subpackForUpdate.setName(null);
        subpackDao.update(subpackForUpdate);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void updateNonexistingSubpack() throws Exception {
        subpackDao.update(sampleAnimal02Subpack().id((long) 25).build());
    }
    
    @Test(expected = ValidationException.class)
    public void updateSubpackEmptyName() throws Exception {
        Subpack subpackForUpdate = sampleAnimal01Subpack().build();
        subpackDao.create(subpackForUpdate);
        subpackForUpdate.setName("");
        subpackDao.update(subpackForUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updateSubpackNullId() throws Exception {
        subpackDao.update(sampleAnimal01Subpack().id(null).build());
    }
    
    @Test(expected = ValidationException.class)
    public void updatePersonWithNegativeId() throws Exception {
        Subpack subpackWithNegativeId = sampleAnimal01Subpack().id((long) -1).build();
        subpackDao.update(subpackWithNegativeId);
    }
    
    @Test
    public void getById() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        assertThat(subpackDao.getById(subpack.getId()))
                .isNotNull()
                .isEqualToComparingFieldByField(subpack);
    }
    
    @Test
    public void getAll() throws Exception {
        Subpack subpackAnimal = sampleAnimal01Subpack().build();
        Subpack subpackThing = sampleThingSubpack().build();
        subpackDao.create(subpackAnimal);
        subpackDao.create(subpackThing);
        assertThat(subpackDao.getAll())
                .usingFieldByFieldElementComparator()
                .containsOnly(subpackAnimal, subpackThing);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNullId() throws Exception {
        subpackDao.getById(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNegativeId() throws Exception {
        subpackDao.getById((long) -1);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void getByNonexistingId() throws Exception {
        subpackDao.getById((long) 25);
    }
    
    @Test
    public void assignPersonToSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, pepePerson);
        assertThat(subpackDao.getPeopleAssignedToSubpack(subpack))
                .usingFieldByFieldElementComparator()
                .containsOnly(pepePerson);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void assignNullPersonToSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void assignPersonToNullSubpack() throws Exception {
        subpackDao.assignPersonToSubpack(null, pepePerson);
    }
    
    @Test(expected = ValidationException.class)
    public void assignPersonWithNullIdToSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, personWithNullId);
    }
    
    @Test(expected = ValidationException.class)
    public void assignPersonToSubpackWithNullId() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.assignPersonToSubpack(subpack, personWithNullId);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void assignNonexistingPersonToSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, personNotInDB);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void assignPersonToNonexistingSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpack.setId((long) 100);
        subpackDao.assignPersonToSubpack(subpack, personNotInDB);
    }
    
    @Test
    public void assignPeopleToSubpack() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, pepePerson);
        subpackDao.updateAssignment(subpack, Arrays.asList(leonPerson));
        assertThat(subpackDao.getPeopleAssignedToSubpack(subpack))
                .usingFieldByFieldElementComparator()
                .containsOnly(leonPerson);
    }

    @Test
    public void getSubpacksInPack() throws Exception {
        Subpack subpack01 = sampleAnimal01Subpack().build();
        Subpack subpack02 = sampleAnimal02Subpack().build();
        subpackDao.create(subpack01);
        subpackDao.create(subpack02);
        assertThat(subpackDao.getSubpacksInPack(animalPack))
                .usingFieldByFieldElementComparator()
                .containsOnly(subpack01, subpack02);
    }
    
    @Test
    public void getNoSubpackInPack() throws Exception {
        assertThat(subpackDao.getSubpacksInPack(thingPack))
                .isEmpty();
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void getSubpacksInUnknownPack() throws Exception {
        subpackDao.getSubpacksInPack(packNotInDB);
    }
    
    @Test(expected = ValidationException.class)
    public void getSubpacksInPackWithNullId() throws Exception {
        subpackDao.getSubpacksInPack(packWithNullId);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getSubpacksInNullPack() throws Exception {
        subpackDao.getSubpacksInPack(null);
    }
    
    @Test
    public void doesExist() throws Exception {
        Subpack existingSubpack = sampleAnimal01Subpack().build();
        subpackDao.create(existingSubpack);
        assertTrue(subpackDao.doesExist(existingSubpack));
        assertFalse(subpackDao.doesExist(sampleAnimal01Subpack().id((long) 250).build()));
    }
    
    @Test
    public void createRepeatAnswer() throws Exception {
        Subpack subpack01 = sampleAnimal01Subpack().build();
        Subpack subpack02 = sampleAnimal02Subpack().build();
        subpackDao.create(subpack01);
        subpackDao.create(subpack02);
        Answer answerDog = new Answer(subpack01, "dog", Boolean.FALSE);
        Answer answerElephant = new Answer(subpack01, "elephant", Boolean.FALSE);
        Answer answerCat = new Answer(subpack01, "cat", Boolean.FALSE);
        Answer answerChicken = new Answer(subpack01, "chicken", Boolean.FALSE);
        Answer answerFrog = new Answer(subpack01, "frog", Boolean.FALSE);
        Answer answerKangoo = new Answer(subpack01, "kangoo", Boolean.FALSE);
        Answer answerDeer = new Answer(subpack01, "deer", Boolean.FALSE);
        Answer answerNoise = new Answer(subpack01, "adfadf", Boolean.TRUE);
        Answer answerPig = new Answer(subpack02, "pig", Boolean.FALSE);
        Answer answerBird = new Answer(subpack02, "bird", Boolean.FALSE);
        Answer answerRabit = new Answer(subpack02, "rabit", Boolean.FALSE);
        answerDao.create(answerDog);
        answerDao.create(answerElephant);
        answerDao.create(answerCat);
        answerDao.create(answerChicken);
        answerDao.create(answerFrog);
        answerDao.create(answerKangoo);
        answerDao.create(answerDeer);
        answerDao.create(answerNoise);
        answerDao.create(answerPig);
        answerDao.create(answerBird);
        answerDao.create(answerRabit);
        
        answerDao.createRepeatingAnswer(answerDog);
        answerDao.createRepeatingAnswer(answerDog);
        answerDao.createRepeatingAnswer(answerElephant);
        answerDao.createRepeatingAnswer(answerRabit);
        
        assertThat(answerDao.getAnswersInSubpack(subpack01))
                    .hasSize(8+3);
    }
    
    @Test
    public void deleteAssignation() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, leonPerson);
        subpackDao.assignPersonToSubpack(subpack, pepePerson);
        assertThat(subpackDao.getAssignationTime(subpack, pepePerson))
                .isNotNull();
        assertThat(subpackDao.getAssignationTime(subpack, leonPerson))
                .isNotNull();
        subpackDao.deleteAssignmentPersonToSubpack(subpack, pepePerson);
        assertThat(subpackDao.getAssignationTime(subpack, leonPerson))
                .isNotNull();
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void getDeletedAssignation() throws Exception {
        Subpack subpack = sampleAnimal01Subpack().build();
        subpackDao.create(subpack);
        subpackDao.assignPersonToSubpack(subpack, pepePerson);
        assertThat(subpackDao.getAssignationTime(subpack, pepePerson))
                .isNotNull();
        subpackDao.deleteAssignmentPersonToSubpack(subpack, pepePerson);
        assertThat(subpackDao.getAssignationTime(subpack, leonPerson))
                .isNotNull();
    }
}