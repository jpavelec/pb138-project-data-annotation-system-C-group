package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
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
public class PackDaoImplTest {

    @Inject
    private DataSource dataSource;
    
    @Inject
    private PackDaoImpl packDao;
    
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
                .noiseRate(0.99)
                .repeatingRate(15.95);
    }
    
    @Test
    public void createPack() throws Exception {
        Pack pack = sampleAnimalPack().build();
        packDao.create(pack);
        Long packId = pack.getId();
        assertThat(packId).isNotNull();
        assertThat(packDao.getById(packId))
                .isNotSameAs(pack)
                .isEqualToComparingFieldByField(pack);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullPack() throws Exception {
        packDao.create(null);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithExistingId() throws Exception {
        Pack pack = sampleAnimalPack().id((long) 1).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackLongDecimalPartNoiseRating() throws Exception {
        Pack pack = sampleAnimalPack().noiseRate(2.347).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackLongDecimalPartRepeatRating() throws Exception {
        Pack pack = sampleAnimalPack().repeatingRate(3.845).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithBigNoiseRating() throws Exception {
        Pack pack = sampleAnimalPack().noiseRate(100.01).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithBigRepeatRating() throws Exception {
        Pack pack = sampleAnimalPack().repeatingRate(100.01).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithNegativeRepeatRating() throws Exception {
        Pack pack = sampleAnimalPack().repeatingRate(-0.01).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithNegativeNoiseRating() throws Exception {
        Pack pack = sampleAnimalPack().noiseRate(-0.01).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithNullQuestion() throws Exception {
        Pack pack = sampleAnimalPack().question(null).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyQuestion() throws Exception {
        Pack pack = sampleAnimalPack().question("").build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithLongQuestion() throws Exception {
        Pack pack = sampleAnimalPack().question(
            "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj" +
            "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj" +
            "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj" +
            "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj" +
            "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjjk").build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithNullName() throws Exception {
        Pack pack = sampleAnimalPack().name(null).build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyName() throws Exception {
        Pack pack = sampleAnimalPack().name("").build();
        packDao.create(pack);
    }
    
    @Test(expected = ValidationException.class)
    public void createPackWithLongName() throws Exception {
        Pack pack = sampleAnimalPack().name(
            "aaaaabbbbbcccccdddddeeeeefffffg").build();
        packDao.create(pack);
    }
    
    @Test
    public void updatePack() throws Exception {
        Pack packForUpdate = sampleAnimalPack().build();
        Pack anotherPack = sampleThingPack().build();
        packDao.create(packForUpdate);
        packDao.create(anotherPack);
        
        packForUpdate.setQuestion("Is it a wild animal?");
        packForUpdate.setRepeatingRate(5.25);
        packDao.update(packForUpdate);
        
        assertThat(packDao.getById(packForUpdate.getId()))
                .isEqualToComparingFieldByField(packForUpdate);
        assertThat(packDao.getById(anotherPack.getId()))
                .isEqualToComparingFieldByField(anotherPack);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateNullPack() throws Exception {
        packDao.update(null);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithNullId() throws Exception {
        packDao.update(sampleThingPack().id(null).build());
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithNegativeId() throws Exception {
        packDao.update(sampleThingPack().id((long) -1).build());
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void updateNonexistingPack() throws Exception {
        packDao.update(sampleThingPack().id((long) 25).build());
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithNullQuestion() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setQuestion(null);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithNullName() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setName(null);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithEmptyQuestion() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setQuestion("");
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithEmptyName() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setName("");
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithNegativeNoiseRatio() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setNoiseRate(-1);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithNegativeRepeatingRatio() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setRepeatingRate(-1);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithBigNoiseRatio() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setNoiseRate(100.01);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackWithBigRepeatingRatio() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setRepeatingRate(100.01);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackLongDecimalPartNoiseRating() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setNoiseRate(1.002);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = ValidationException.class)
    public void updatePackLongDecimalPartRepeatingRatio() throws Exception {
        Pack packToUpdate = sampleAnimalPack().build();
        packDao.create(packToUpdate);
        packToUpdate.setRepeatingRate(2.238);
        packDao.update(packToUpdate);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteNullPack() throws Exception {
        packDao.delete(null);
    }
    
    @Test(expected = ValidationException.class)
    public void deletePackWithNullId() throws Exception {
        packDao.delete(sampleAnimalPack().id(null).build());
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void deleteNonexistingPack() throws Exception {
        packDao.delete(sampleAnimalPack().id((long) 25).build());
    }
    
    @Test
    public void deletePack() throws Exception {
        Pack packAnimal = sampleAnimalPack().build();
        Pack packThing = sampleThingPack().build();
        packDao.create(packAnimal);
        packDao.create(packThing);
        packDao.delete(packAnimal);
        assertThat(packDao.getAll())
                .usingFieldByFieldElementComparator()
                .containsOnly(packThing);
    }
    
    @Test
    public void getById() throws Exception {
        Pack packAnimal = sampleAnimalPack().build();
        Pack packThing = sampleThingPack().build();
        packDao.create(packAnimal);
        packDao.create(packThing);
        assertThat(packDao.getById(packAnimal.getId()))
                .isEqualToComparingFieldByField(packAnimal);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNegativeId() throws Exception {
        packDao.getById((long) -1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNullId() throws Exception {
        packDao.getById(null);
    }
    
    @Test(expected = BeanNotExistsException.class)
    public void getByNonexistingId() throws Exception {
        packDao.getById((long) 25);
    }
    
    @Test
    public void getAllPeople() throws Exception {
        assertThat(packDao.getAll()).isEmpty();
        Pack packAnimal = sampleAnimalPack().build();
        Pack packThing = sampleThingPack().build();
        packDao.create(packAnimal);
        packDao.create(packThing);
        assertFalse(packDao.getAll().isEmpty());
        assertThat(packDao.getAll())
            .usingFieldByFieldElementComparator()
            .containsOnly(packAnimal, packThing);
    }
    
    @Test
    public void doesExist() throws Exception {
        Pack existingPack = sampleAnimalPack().build();
        packDao.create(existingPack);
        assertTrue(packDao.doesExist(existingPack));
        assertFalse(packDao.doesExist(sampleAnimalPack().id((long) 250).build()));
    }
    
}