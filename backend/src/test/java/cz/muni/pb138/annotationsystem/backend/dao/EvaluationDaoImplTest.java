package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
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
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EvaluationDaoImplTest {

    @Inject
    private DataSource dataSource;
    
    @Inject
    private PersonDaoImpl personDao;
    
    @Inject
    private PackDaoImpl packDao;
    
    @Inject
    private SubpackDaoImpl subpackDao;
    
    @Inject
    private AnswerDaoImpl answerDao;
    
    @Inject
    private EvaluationDaoImpl evalDao;

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
    
    private Person frankPerson, janePerson;
    private Pack animalPack, thingPack;
    private Subpack animalSubpack01, animalSubpack02, animalSubpack03;
    private Subpack thingSubpack01, thingSubpack02, thingSubpack03;
    private Answer dogAnswer, deerAnswer, catAnswer, chickenAnswer, garlicAnswer, poisonAnswer;
    private Answer tableAnswer, chairAnswer, windowAnswer, vegetableAnswer, glassAnswer, cupAnswer;
    private Evaluation frankEvalDogAnswer, frankEvalDeerAnswer, frankEvalCatAnswer;
    private Evaluation janeEvalDogAnswer, janeEvalWindowAnswer, janeEvalGlassAnswer;
    
    private void prepareTestData() throws Exception {
        frankPerson = new Person("Frank");
        janePerson = new Person("Jane");
        personDao.create(frankPerson);
        personDao.create(janePerson);
        
        animalPack = new Pack("Is it an animal?", "animal", 2.2, 3.5);
        thingPack = new Pack("Is it a thing?", "thing", 15.85, 20.35);
        packDao.create(animalPack);
        packDao.create(thingPack);
        
        animalSubpack01 = new Subpack(animalPack, "animal_01");
        animalSubpack02 = new Subpack(animalPack, "animal_02");
        animalSubpack03 = new Subpack(animalPack, "animal_03");
        thingSubpack01 = new Subpack(thingPack, "thing_01");
        thingSubpack02 = new Subpack(thingPack, "thing_02");
        thingSubpack03 = new Subpack(thingPack, "thing_03");
        subpackDao.create(animalSubpack01);
        subpackDao.create(animalSubpack02);
        subpackDao.create(animalSubpack03);
        subpackDao.create(thingSubpack01);
        subpackDao.create(thingSubpack02);
        subpackDao.create(thingSubpack03);
        
        dogAnswer = new Answer(animalSubpack01, "dog", false);
        deerAnswer = new Answer(animalSubpack01, "deer", false);
        catAnswer = new Answer(animalSubpack02, "cat", false);
        chickenAnswer = new Answer(animalSubpack03, "chicken", true);
        garlicAnswer = new Answer(animalSubpack02, "garlic", false);
        poisonAnswer = new Answer(animalSubpack03, "poison", true);
        answerDao.create(dogAnswer);
        answerDao.create(deerAnswer);
        answerDao.create(catAnswer);
        answerDao.create(chickenAnswer);
        answerDao.create(garlicAnswer);
        answerDao.create(poisonAnswer);
        
        tableAnswer = new Answer(thingSubpack01, "table", false);
        chairAnswer = new Answer(thingSubpack01, "chair", false);
        windowAnswer = new Answer(thingSubpack02, "window", false);
        vegetableAnswer = new Answer(thingSubpack02, "vegetable", true);
        glassAnswer = new Answer(thingSubpack03, "glass", false);
        cupAnswer = new Answer(thingSubpack03, "cup", false);
        answerDao.create(tableAnswer);
        answerDao.create(chairAnswer);
        answerDao.create(windowAnswer);
        answerDao.create(vegetableAnswer);
        answerDao.create(glassAnswer);
        answerDao.create(cupAnswer);
        
        frankEvalDogAnswer = new Evaluation(frankPerson, dogAnswer, Rating.NEGATIVE, 2500);
        frankEvalDeerAnswer = new Evaluation(frankPerson, deerAnswer, Rating.NEGATIVE, 2500);
        frankEvalCatAnswer = new Evaluation(frankPerson, catAnswer, Rating.NEGATIVE, 2500);
        janeEvalDogAnswer = new Evaluation(janePerson, dogAnswer, Rating.NEGATIVE, 2500);
        janeEvalWindowAnswer = new Evaluation(janePerson, windowAnswer, Rating.NEGATIVE, 2500);
        janeEvalGlassAnswer = new Evaluation(janePerson, glassAnswer, Rating.NEGATIVE, 2500);
        evalDao.create(frankEvalDogAnswer);
        evalDao.create(frankEvalDeerAnswer);
        evalDao.create(frankEvalCatAnswer);
        evalDao.create(janeEvalDogAnswer);
        evalDao.create(janeEvalWindowAnswer);
        evalDao.create(janeEvalGlassAnswer);
    }

    @Test
    public void testDoesExist() throws Exception {
        Evaluation existingEvaluation = new Evaluation(frankPerson, dogAnswer, Rating.POSITIVE, 2500);
        evalDao.create(existingEvaluation);
        Evaluation nonExistingEvaluation = new Evaluation(frankPerson, dogAnswer, Rating.NEGATIVE, 2500);
        nonExistingEvaluation.setId((long) 54);
        assertTrue(evalDao.doesExist(existingEvaluation));
        assertFalse(evalDao.doesExist(nonExistingEvaluation));
    }
    
    @Test
    public void testDeletePack() throws Exception {
        assertThat(evalDao.getAll())
                .hasSize(6);
        evalDao.delete(frankEvalCatAnswer);
        assertThat(evalDao.getAll())
                .hasSize(5);
        assertThat(answerDao.getAll())
                .hasSize(12);
        answerDao.delete(dogAnswer);
        assertThat(answerDao.getAll())
                .hasSize(11);
        assertThat(evalDao.getAll())
                .hasSize(3);
                
    }

}