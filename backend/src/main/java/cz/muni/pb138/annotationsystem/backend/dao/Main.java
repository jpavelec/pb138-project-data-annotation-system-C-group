package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class Main {
    
    private static final int PACK_SIZE = 20;
    
    final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();
        //set JDBC driver and URL
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl("jdbc:derby:memory:backend;create=true");
        //populate db with tables and data
        new ResourceDatabasePopulator(
                new ClassPathResource("createTables.sql"),
                new ClassPathResource("testData.sql"))
                .execute(bds);
        return bds;
    }

    public static void main(String[] args) throws DaoException {
        
        DataSource dataSource = createMemoryDatabase();
        
        
        BufferedReader br = null;
        
        try {
            String line;
            br = new BufferedReader(new FileReader("/home/ondrejvelisek/projects/annotation-system/backend/src/main/resources/animal.txt"));
            
            if ((line = br.readLine()) != null) {
                Pack pack = new Pack();
                pack.setQuestion(line);
                pack.setName("animal");
                pack.setRepeating(3);
                pack.setNoiseRate(0);
                PackDao packDao = new PackDao(dataSource);
                packDao.create(pack);
                
                int numberOfPack = 0;
                int countAnswersInSubpack = PACK_SIZE;
                SubpackDao subpackDao = new SubpackDao(dataSource);
                AnswerDao answerDao = new AnswerDaoImpl(dataSource);
                Subpack subpack = new Subpack();
                subpack.setParent(pack);
                Answer answer = new Answer();
                while ((line = br.readLine()) != null) {
                    if (countAnswersInSubpack == PACK_SIZE) {
                        subpack.setId(null);
                        //subpack.setName("A"+Integer.toString(numberOfPack));
                        subpackDao.create(subpack);
                        countAnswersInSubpack = 0;
                        numberOfPack++;
                    }
                    answer.setId(null);
                    answer.setFromSubpack(subpack);
                    answer.setAnswer(line);
                    answer.setIsNoise(false);
                    answerDao.create(answer);
                    System.out.println(answer);
                    countAnswersInSubpack++;
                    
                }
            }
            
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        System.out.println("Nacteno");
        
        /*LOG.info("starting");

        DataSource dataSource = createMemoryDatabase();
        PackDao packDao = new PackDao(dataSource);
        SubpackDao subpackDao = new SubpackDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        EvaluationDao evaluationDao = new EvaluationDao(dataSource);
        PersonDao personDao = new PersonDao(dataSource);

        Long id1 = new Long(1);
        Long id2 = new Long(2);
        Long id3 = new Long(3);
        Long id4 = new Long(4);
        
        Pack pack = new Pack();
        pack.setQuestion("Jsem to ja?");
        pack.setName("ja");
        pack.setNoiseRate(3);
        pack.setRepeating(4);
        packDao.create(pack);
        System.out.println(pack);
        
        Subpack subpack = new Subpack();
        subpack.setParent(pack);
        subpack.setName("J01");
        subpackDao.create(subpack);
        System.out.println(subpack);

        Answer answer = new Answer();
        answer.setFromSubpack(subpack);
        answer.setAnswer("student");
        answer.setIsNoise(Boolean.FALSE);
        answerDao.create(answer);
        System.out.println(answer);
        
        Person person = new Person();
        person.setUsername("Golias Velky");
        person.setIsAdmin(false);
        personDao.create(person);
        System.out.println(person);
        
        
        Evaluation eval = new Evaluation();
        eval.setPerson(person);
        eval.setAnswer(answer);
        eval.setRating(Rating.POSITIVE);
        eval.setElapsedTime(2500);
        evaluationDao.create(eval);
        System.out.println(eval);*/
        
        //eval = evaluationDao.getById(id1);
        //System.out.println(answerDao.getById(id1));
        
    }
}
