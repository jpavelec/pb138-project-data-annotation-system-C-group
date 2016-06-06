package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
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

import static org.junit.Assert.*;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EvaluationManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private EvaluationManager evaluationManager;

    @Inject
    private AnswerManager answerManager;

    @Inject
    private PackManager packManager;

    @Inject
    private PersonManager personManager;

    @Inject
    private SubpackManager subpackManager;

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
    public void eval() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);

        assertNotNull(eval.getId());
        assertEquals(eval, evaluationManager.getEvaluationById(eval.getId()));
        assertTrue(evaluationManager.getEvaluationsOfPerson(persons[1]).contains(eval));
    }
    @Test(expected = ValidationException.class)
    public void evalWithId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        eval.setId((long) 1);

        evaluationManager.eval(eval);
    }
    @Test(expected = IllegalStateException.class)
    public void evalNotAssigned() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);

        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        subpackManager.updatePersonsAssignment(persons[1], packs[1], new ArrayList<Subpack>());

        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);
    }
    @Test(expected = IllegalArgumentException.class)
    public void evalNullEval() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);

        evaluationManager.eval(null);
    }
    @Test(expected = ValidationException.class)
    public void evalNullAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);

        Evaluation eval = new Evaluation(persons[1], null, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }
    @Test(expected = ValidationException.class)
    public void evalNullPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        Evaluation eval = new Evaluation(null, answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }
    @Test(expected = ValidationException.class)
    public void evalNonExistsAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        answer.setId(TestUtils.UNKNOWN_ID);
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }
    @Test(expected = ValidationException.class)
    public void evalNonExistsPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        persons[1].setId(TestUtils.UNKNOWN_ID);
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }


    @Test
    public void correct() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);

        evaluationManager.correct(correct);

        Evaluation result = evaluationManager.getEvaluationById(eval.getId());

        assertEquals(correct.getId(), result.getId());
        assertEquals(Rating.NEGATIVE, result.getRating());
        assertEquals(correct.getElapsedTime(), result.getElapsedTime());
        assertTrue(evaluationManager.getEvaluationsOfPerson(persons[1]).contains(result));
    }
    @Test(expected = ValidationException.class)
    public void correctWithoutId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);
        correct.setId(null);

        evaluationManager.correct(correct);
    }
    @Test(expected = BeanNotExistsException.class)
    public void correctUnknownEval() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);
        correct.setId(TestUtils.UNKNOWN_ID);

        evaluationManager.correct(correct);
    }
    @Test(expected = IllegalStateException.class)
    public void correctNotAssigned() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);

        subpackManager.updatePersonsAssignment(persons[1], packs[1], new ArrayList<Subpack>());

        evaluationManager.correct(correct);
    }
    @Test(expected = IllegalArgumentException.class)
    public void correctNullEval() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);

        evaluationManager.correct(null);

    }
    @Test(expected = ValidationException.class)
    public void correctNullAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);
        correct.setAnswer(null);

        evaluationManager.correct(correct);
    }
    @Test(expected = ValidationException.class)
    public void correctNullPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);
        correct.setPerson(null);

        evaluationManager.correct(correct);
    }
    @Test(expected = ValidationException.class)
    public void correctNonExistsAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);
        correct.getAnswer().setId(TestUtils.UNKNOWN_ID);

        evaluationManager.correct(correct);
    }
    @Test(expected = ValidationException.class)
    public void correctNonExistsPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);

        Evaluation correct = evaluationManager.getEvaluationById(eval.getId());
        correct.setRating(Rating.NEGATIVE);
        correct.getPerson().setId(TestUtils.UNKNOWN_ID);

        evaluationManager.correct(correct);
    }



    @Test
    public void getEvaluationById() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        Evaluation result = evaluationManager.getEvaluationById(evals[1].getId());

        assertEquals(evals[1], result);
        assertEquals(evals[1].getId(), result.getId());
        assertEquals(evals[1].getElapsedTime(), result.getElapsedTime());
        assertEquals(evals[1].getAnswer(), result.getAnswer());
        assertEquals(evals[1].getPerson(), result.getPerson());
        assertEquals(evals[1].getRating(), result.getRating());
        assertTrue(evaluationManager.getEvaluationsOfPerson(persons[1]).contains(result));
    }
    @Test(expected = IllegalArgumentException.class)
    public void getEvaluationByIdNull() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        evaluationManager.getEvaluationById(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getEvaluationByIdNegativeId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        evaluationManager.getEvaluationById((long) -1);
    }
    @Test(expected = BeanNotExistsException.class)
    public void getEvaluationByIdUnknownId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        evaluationManager.getEvaluationById(TestUtils.UNKNOWN_ID);
    }



    @Test
    public void getEvaluationsOfPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        List<Evaluation> result = evaluationManager.getEvaluationsOfPerson(persons[1]);

        assertEquals(new HashSet<>(Arrays.asList(evals)), new HashSet<>(result));
    }
    @Test(expected = IllegalArgumentException.class)
    public void getEvaluationsOfPersonNullPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        evaluationManager.getEvaluationsOfPerson(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getEvaluationsOfPersonPersonWithoutId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        evaluationManager.getEvaluationsOfPerson(TestUtils.getPerson1());
    }
    @Test(expected = BeanNotExistsException.class)
    public void getEvaluationsOfPersonUnknownPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], packs[1], subpacks);
        Evaluation[] evals = TestUtils.createEvals(evaluationManager, answerManager, persons[1], subpacks.get(0));

        Person person = TestUtils.getPerson1();
        person.setId(TestUtils.UNKNOWN_ID);
        evaluationManager.getEvaluationsOfPerson(person);
    }

}