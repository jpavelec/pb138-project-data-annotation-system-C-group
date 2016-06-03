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
    
    private Person frankPerson;
    private Pack animalPack;
    private Subpack animalSubpack;
    private Answer dogAnswer;
    
    private void prepareTestData() throws Exception {
        frankPerson = new Person("Frank");
        personDao.create(frankPerson);
        animalPack = new Pack("Is it an animal?", "animal", 2.2, 3.5);
        packDao.create(animalPack);
        animalSubpack = new Subpack(animalPack, "animal_01");
        subpackDao.create(animalSubpack);
        dogAnswer = new Answer(animalSubpack, "dog", false);
        answerDao.create(dogAnswer);
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

}