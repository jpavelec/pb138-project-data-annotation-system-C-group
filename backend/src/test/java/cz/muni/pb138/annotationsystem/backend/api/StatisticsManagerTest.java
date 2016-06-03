package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class StatisticsManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private StatisticsManager statisticsManager;

    @Inject
    private PersonManager personManager;
    @Inject
    private PackManager packManager;
    @Inject
    private SubpackManager subpackManager;
    @Inject
    private AnswerManager answerManager;
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
    public void getProgressOfPack() throws Exception {
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        double result = statisticsManager.getProgressOfPack(pack);
        assertEquals(100.0*23.0/96.0, result, TestUtils.EPSILON);
    }


    @Test
    public void getProgressOfSubpack() throws Exception {
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        Subpack subpack = subpackManager.getSubpacksInPack(pack).get(1);
        double result = statisticsManager.getProgressOfSubpack(subpack);
        assertEquals(100.0*15.0/48.0, result, TestUtils.EPSILON);
    }


    @Test
    public void getProgressOfSubpackForPerson() throws Exception {
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        Subpack subpack = subpackManager.getSubpacksInPack(pack).get(2);
        Person carl = personManager.getOrCreatePersonByUsername("carl");
        double result = statisticsManager.getProgressOfSubpackForPerson(subpack, carl);
        assertEquals(100.0*8.0/24.0, result, TestUtils.EPSILON);
    }

    @Test
    public void getCohenKappa() throws Exception {
        // Hard to test it properly
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        Person carl = personManager.getOrCreatePersonByUsername("carl");
        Double result = statisticsManager.getCohenKappa(carl);
        assertTrue(result == null || result <= 1);
    }

    @Test
    public void getCohenKappa1() throws Exception {
        // Hard to test it properly
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        Subpack subpack = subpackManager.getSubpacksInPack(pack).get(2);
        Person carl = personManager.getOrCreatePersonByUsername("carl");
        Double result = statisticsManager.getCohenKappa(carl, subpack);
        assertTrue(result == null || result <= 1);
    }

    @Test
    public void averageEvaluationTimeOfSubpack() throws Exception {
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        Subpack subpack = subpackManager.getSubpacksInPack(pack).get(1);
        double result = statisticsManager.averageEvaluationTimeOfSubpack(subpack);
        assertEquals((6*11+5*14+4*22)/15.0, result, TestUtils.EPSILON);
    }

    @Test
    public void averageEvaluationTimeOfSubpackForPerson() throws Exception {
        Pack pack = prepareEnvironment(personManager, packManager, subpackManager, answerManager, evaluationManager)[0];
        Subpack subpack = subpackManager.getSubpacksInPack(pack).get(2);
        Person carl = personManager.getOrCreatePersonByUsername("carl");
        double result = statisticsManager.averageEvaluationTimeOfSubpackForPerson(subpack, carl);
        assertEquals((3*11+2*14+3*22)/8.0, result, TestUtils.EPSILON);
    }

    @Ignore
    @Test
    public void averageCompletionTimeOfSubpack() throws Exception {
        fail("No idea how should be tested");
    }




    private Pack[] prepareEnvironment(PersonManager personManager, PackManager packManager, SubpackManager subpackManager,
            AnswerManager answerManager, EvaluationManager evaluationManager) throws Exception {

        Person anna = personManager.getOrCreatePersonByUsername("anna");
        Person carl = personManager.getOrCreatePersonByUsername("carl");
        Person phil = personManager.getOrCreatePersonByUsername("phil");

        Pack[] packs = TestUtils.createPacks(packManager);
        Pack animalsPack = packs[0];
        Pack wordPack = packs[1];
        Pack namePack = packs[2];

        List<Subpack> annasSubpacks = new ArrayList<>();
        annasSubpacks.addAll(subpackManager.getSubpacksInPack(animalsPack).subList(0, 2));
        subpackManager.updatePersonsAssignment(anna, annasSubpacks);

        List<Subpack> carlsSubpacks = new ArrayList<>();
        carlsSubpacks.addAll(subpackManager.getSubpacksInPack(animalsPack).subList(1, 3));
        subpackManager.updatePersonsAssignment(carl, carlsSubpacks);


        Answer annaAnswer;
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.POSITIVE, 11));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.POSITIVE, 11));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.NEGATIVE, 22));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.NONSENSE, 22));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.POSITIVE, 14));

        Answer carlAnswer;
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 11));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 11));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 14));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 14));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 14));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 14));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 22));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 22));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 11));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 11));


        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 11));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 14));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 11));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 14));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 22));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 11));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 22));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 22));

        return new Pack[]{animalsPack, wordPack, namePack};
    }


}