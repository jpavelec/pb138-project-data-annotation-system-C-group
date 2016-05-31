package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.dao.PackDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PackManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private PackManager packManager;

    @Inject
    private SubpackManager subpackManager;

    @Inject
    private AnswerManager answerManager;

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
    public void createPack() throws Exception {

        Pack pack0 = TestUtils.getPack0();
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);

        assertNotNull(pack0.getId());

        assertTrue(packManager.getAllPacks().size() == 1);

        assertEquals(pack0, packManager.getPackById(pack0.getId()));

        List<Answer> answers = answerManager.getAnswersInPack(pack0);

        assertTrue(TestUtils.getAnswers0().size() == answers.size());
        for (Answer answer : answers) {
            assertTrue(TestUtils.getAnswers0().contains(answer.getAnswer()));
        }

        List<Subpack> subpacks = subpackManager.getSubpacksInPack(pack0);

        assertTrue(subpacks.size() == Math.ceil(TestUtils.getAnswers0().size() / ((double)TestUtils.SUBPACK_SIZE_0)));

        int lessThanSubpackSize = 0;
        List<Answer> previousSubanswers = null;
        List<Answer> subanswers = null;
        for (Subpack subpack : subpacks) {
            previousSubanswers = subanswers;
            subanswers = answerManager.getAnswersInSubpack(subpack);

            if (subanswers.size() < TestUtils.SUBPACK_SIZE_0) {
                lessThanSubpackSize++;
            }

            for (Answer subanswer : subanswers) {
                assertTrue(TestUtils.getAnswers0().contains(subanswer.getAnswer()));
            }

            if (previousSubanswers != null) {
                assertTrue(Collections.disjoint(previousSubanswers, subanswers));
            }
        }

        assertTrue(lessThanSubpackSize <= 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPackNullPack() throws Exception {
        packManager.createPack(null, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = IllegalArgumentException.class)
    public void createPackNullAnswers() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        packManager.createPack(pack0, null, TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = IllegalArgumentException.class)
    public void createPackEmptyAnswers() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        packManager.createPack(pack0, new ArrayList<String>(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = IllegalArgumentException.class)
    public void createPackNullNoise() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        packManager.createPack(pack0, TestUtils.getAnswers0(), null, TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = IllegalArgumentException.class)
    public void createPackZeroOrLessSubpackSize() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), 0);
    }

    @Test(expected = ValidationException.class)
    public void createPackWithId() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setId((long) 1);
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackNegativeNoiseRate() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setNoiseRate(-0.1);
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithoutName() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setName(null);
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyName() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setName("");
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithoutQuestion() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setQuestion(null);
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyQuestion() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setQuestion("");
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithNegativeRepeatingRate() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setRepeatingRate(-0.1);
        packManager.createPack(pack0, TestUtils.getAnswers0(), TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithNullStringAsAnswer() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        List<String> answers = new LinkedList<>(TestUtils.getAnswers0());
        answers.add(null);
        packManager.createPack(pack0, answers, TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyStringAsAnswer() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        List<String> answers = new LinkedList<>(TestUtils.getAnswers0());
        answers.add("");
        packManager.createPack(pack0, answers, TestUtils.getNoise0(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithNullStringAsNoise() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        List<String> noise = new LinkedList<>(TestUtils.getNoise0());
        noise.add(null);
        packManager.createPack(pack0, TestUtils.getAnswers0(), noise, TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyStringAsNoise() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        List<String> noise = new LinkedList<>(TestUtils.getNoise0());
        noise.add("");
        packManager.createPack(pack0, TestUtils.getAnswers0(), noise, TestUtils.SUBPACK_SIZE_0);
    }
    @Test // It is OK.
    public void createPackWithEmptyNoiseAndZeroRate() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setNoiseRate(0);
        packManager.createPack(pack0, TestUtils.getAnswers0(), new ArrayList<String>(), TestUtils.SUBPACK_SIZE_0);
    }
    @Test(expected = ValidationException.class)
    public void createPackWithEmptyNoiseAndPositiveRate() throws Exception {
        Pack pack0 = TestUtils.getPack0();
        pack0.setNoiseRate(0.1);
        packManager.createPack(pack0, TestUtils.getAnswers0(), new ArrayList<String>(), TestUtils.SUBPACK_SIZE_0);
    }




    @Test
    public void getPackById() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);

        Pack result = packManager.getPackById(packs[1].getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(packs[1].getId(), result.getId());
        assertEquals(packs[1].getName(), result.getName());
        assertEquals(packs[1].getNoiseRate(), result.getNoiseRate(), TestUtils.EPSILON);
        assertEquals(packs[1].getQuestion(), result.getQuestion());
        assertEquals(packs[1].getRepeatingRate(), result.getRepeatingRate(), TestUtils.EPSILON);

        assertEquals(packs[1], result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPackByIdWithoutId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        packManager.getPackById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPackByIdWithNegativeId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        packManager.getPackById((long) -1);
    }

    @Test(expected = BeanNotExistsException.class)
    public void getPackByIdUnknownId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        packManager.getPackById((long) 99);
    }






    @Test
    public void getAllPacks() throws Exception {
        Set<Pack> packs = new HashSet<>(Arrays.asList(TestUtils.createPacks(packManager)));

        Set<Pack> result = new HashSet<>(packManager.getAllPacks());

        assertEquals(packs, result);
    }

    @Test
    public void getAllPacksEmpty() throws Exception {
        Set<Pack> result = new HashSet<>(packManager.getAllPacks());

        assertEquals(new HashSet<Pack>(), result);
    }




    @Test
    public void deletePack() throws Exception {

        Pack[] packs = TestUtils.createPacks(packManager);

        /* TO DO
        ... caused a violation of foreign key constraint.
            Isn't there a subpack which references to parent pack to delete? */
        packManager.deletePack(packs[1]);

        List<Pack> all = packManager.getAllPacks();

        assertTrue(all.size() == (packs.length-1));

        assertFalse(all.contains(packs[1]));

        try {
            packManager.getPackById(packs[1].getId());
            fail("should throw an exception after deleting.");
        } catch (BeanNotExistsException e) {
            // fine here
        }

        try {
            answerManager.getAnswersInPack(packs[1]);
            fail("should throw an exception after deleting.");
        } catch (BeanNotExistsException e) {
            // fine here
        }

        try {
            subpackManager.getSubpacksInPack(packs[1]);
            fail("should throw an exception after deleting.");
        } catch (BeanNotExistsException e) {
            // fine here
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deletePackNullPack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        packManager.deletePack(null);
    }
    @Test(expected = BeanNotExistsException.class)
    public void deletePackUnknownPack() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Pack unknown = new Pack("Am I no one?", "Unknown", 0, 0);
        unknown.setId((long) 99);
        packManager.deletePack(unknown);
    }
    @Test(expected = IllegalArgumentException.class)
    public void deletePackWithoutId() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        Pack unknown = new Pack("Am I no one?", "Unknown", 0, 0);
        packManager.deletePack(unknown);
    }




}
