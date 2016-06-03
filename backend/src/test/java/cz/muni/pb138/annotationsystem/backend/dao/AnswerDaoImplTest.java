package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.SQLException;
import java.util.List;
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
public class AnswerDaoImplTest {
    
    @Inject
    private DataSource dataSource;
    
    @Inject
    private PackDaoImpl packDao;
    
    @Inject
    private SubpackDaoImpl subpackDao;
    
    @Inject
    private AnswerDaoImpl answerDao;

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
    
    private Pack animalPack;
    private Subpack animalSubpack;
    
    private void prepareTestData() throws Exception {
        animalPack = new Pack("Is it an animal?", "animal", 2.2, 3.5);
        packDao.create(animalPack);
        animalSubpack = new Subpack(animalPack, "animal_01");
        subpackDao.create(animalSubpack);
    }

    @Test
    public void testDoesExist() throws Exception {
        Answer existingAnswer = new Answer(animalSubpack, "dog", false);
        answerDao.create(existingAnswer);
        Answer nonExistingAnswer = new Answer(animalSubpack, "deer", false);
        nonExistingAnswer.setId((long) 54);
        assertTrue(answerDao.doesExist(existingAnswer));
        assertFalse(answerDao.doesExist(nonExistingAnswer));
    }
}