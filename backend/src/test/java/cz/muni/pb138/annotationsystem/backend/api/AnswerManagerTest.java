package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.business.AnswerManagerImpl;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import javax.inject.Inject;
import javax.xml.crypto.Data;

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

    @Before
    public void setUp() throws Exception {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("createTables.sql"));
    }

    @After
    public void tearDown() throws Exception {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("dropTables.sql"));
    }


    @Test
    public void nextAnswer() throws Exception {
        answerManager.nextAnswer(null, null);
    }

    @Test
    public void getAnswerById() throws Exception {

    }

    @Test
    public void getAnswersInSubpack() throws Exception {

    }

    @Test
    public void getAnswersInPack() throws Exception {

    }


}