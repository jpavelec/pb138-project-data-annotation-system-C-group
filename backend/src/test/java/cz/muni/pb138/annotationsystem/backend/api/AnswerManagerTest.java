package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.business.AnswerManagerImpl;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import javax.inject.Inject;
import javax.xml.crypto.Data;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AnswerManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private AnswerManager answerManager;

    @Inject
    private PackManager packManager;

    @Inject
    private PersonManager personManager;

    @Inject
    private SubpackManager subpackManager;

    @Inject
    private EvaluationManager evaluationManager;


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
    public void nextAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        List<Answer> answers = answerManager.getAnswersInSubpack(subpacks.get(1));
        assertTrue(answers.contains(answer));

        List<Evaluation> personEvals = evaluationManager.getEvaluationsOfPerson(persons[1]);
        for (Evaluation eval : personEvals) {
            assertNotEquals(eval.getAnswer(), answer);
        }
    }
    @Test(expected = IllegalStateException.class)
    public void nextAnswerNoMoreLeft() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        for (Answer answer : answerManager.getAnswersInSubpack(subpacks.get(1))) {
            evaluationManager.eval(new Evaluation(persons[1], answer, Rating.POSITIVE, 10));
        }

        answerManager.nextAnswer(persons[1], subpacks.get(1));
    }
    @Test(expected = IllegalStateException.class)
    public void nextAnswerNotAssigned() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);

        answerManager.nextAnswer(persons[1], subpacks.get(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void nextAnswerNullPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        answerManager.nextAnswer(null, subpacks.get(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void nextAnswerUnknownPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        Person person = TestUtils.getPerson0();
        person.setId((long) 99);
        answerManager.nextAnswer(person, subpacks.get(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void nextAnswerNullSubpack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        answerManager.nextAnswer(persons[1], null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void nextAnswerUnknownSubpack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        subpacks.get(1).setId((long) 99);
        answerManager.nextAnswer(persons[1], subpacks.get(1));
    }



    @Test
    public void getAnswerById() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        List<Answer> answers = answerManager.getAnswersInPack(packs[1]);

        Answer result = answerManager.getAnswerById(answers.get(1).getId());

        assertNotNull(result);
        assertEquals(answers.get(1).getId(), result.getId());
        assertEquals(answers.get(1).getAnswer(), result.getAnswer());
        assertEquals(answers.get(1).getFromSubpack(), result.getFromSubpack());
        assertEquals(answers.get(1), result);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getAnswerByIdUnknownId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        answerManager.getAnswerById((long) 99);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getAnswerByIdNullId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        answerManager.getAnswerById(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getAnswerByIdNegativeId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        answerManager.getAnswerById((long) -1);
    }




    @Test
    public void getAnswersInSubpack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);

        List<Answer> answers = answerManager.getAnswersInSubpack(subpacks.get(0));

        assertTrue(TestUtils.SUBPACK_SIZE_1 <= answers.size());

        for (Answer answer : answers) {
            assertTrue(TestUtils.getAnswers1().contains(answer.getAnswer()));
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void getAnswersInSubpackNullSubpack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        answerManager.getAnswersInSubpack(null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getAnswersInSubpackUnknownSubpack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        subpacks.get(0).setId((long) 99);
        answerManager.getAnswersInSubpack(subpacks.get(0));
    }


    @Test
    public void getAnswersInPack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);
        List<Answer> answers = answerManager.getAnswersInPack(packs[1]);

        assertTrue(TestUtils.getAnswers1().size() == answers.size());

        for (Answer answer : answers) {
            assertTrue(TestUtils.getAnswers1().contains(answer.getAnswer()));
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void getAnswersInPackNullPack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        answerManager.getAnswersInPack(null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getAnswersInPackUnknownPack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        packs[1].setId((long) 99);
        answerManager.getAnswersInPack(packs[1]);
    }


}