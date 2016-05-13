package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import static cz.muni.pb138.annotationsystem.backend.model.Rating.*;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
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
    
    private static final Rating[] ACTION = {Rating.POSITIVE,
                                            Rating.NEGATIVE,
                                            Rating.NEGATIVE,
                                            Rating.NEGATIVE,
                                            Rating.NONSENSE,
                                            Rating.POSITIVE,
                                            Rating.POSITIVE,
                                            Rating.NEGATIVE,
                                            Rating.NONSENSE,
                                            Rating.POSITIVE};
    
    static final ClassLoader loader = Main.class.getClassLoader();
    
    final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl("jdbc:derby:memory:backend;create=true");
        new ResourceDatabasePopulator(
                new ClassPathResource("createTables.sql"),
                new ClassPathResource("testData.sql"))
                .execute(bds);
        return bds;
    }

    
    public static void main(String[] args) throws DaoException {
        
        DataSource dataSource = createMemoryDatabase();
        
        PackDaoImpl packDao = new PackDaoImpl(dataSource);
        SubpackDaoImpl subpackDao = new SubpackDaoImpl(dataSource);
        AnswerDao answerDao = new AnswerDaoImpl(dataSource);
        EvaluationDaoImpl evalDao = new EvaluationDaoImpl(dataSource);
        PersonDaoImpl personDao = new PersonDaoImpl(dataSource);
        
        try (BufferedReader br = new BufferedReader(new FileReader(
                loader.getResource("animal.txt").getFile())))
        {
            
            String line;
            if ((line = br.readLine()) != null) {
                Pack pack = new Pack();
                pack.setQuestion(line);
                pack.setName("animal");
                pack.setRepeatingRate(3);
                pack.setNoiseRate(0);
                packDao.create(pack);
                
                int numberOfPack = 0;
                int countAnswersInSubpack = PACK_SIZE;
                
                
                Subpack subpack = new Subpack();
                subpack.setParent(pack);
                Answer answer = new Answer();
                while ((line = br.readLine()) != null) {
                    if (countAnswersInSubpack == PACK_SIZE) {
                        subpack.setId(null);
                        subpack.setName("A"+Integer.toString(numberOfPack));
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
        
        Answer answer = new Answer();
        Person user = new Person("Jan Anotator",false);
        
        personDao.create(user);
        Evaluation eval;
        for (int i = 0; i<10; i++) {
            answer = answerDao.getById((long) i+1);
            String question = packDao.getById((long) 1).getQuestion();
            
            eval = new Evaluation(user, answer, ACTION[i], (int) Math.round(1000*Math.random()));
            evalDao.create(eval);
        }
        
        System.out.println("Nacteno");
        System.out.println("Vypisu vsechny vyplnene");
        List<Evaluation> evals = evalDao.getAll();
        for (Evaluation e : evals) {
            System.out.println(e);
        }
        
        System.out.println("Vypisu vsechny otazky ");
        List<Answer> answers = answerDao.getAll();
        for (Answer a : answers) {
            System.out.println(a);
        }
        
        System.out.println("Vypisu vsechny uzivatele ");
        List<Person> users = personDao.getAll();
        for (Person p : users) {
            System.out.println(p);
        }
        
        System.out.println("Vypisu vsechny subpacky ");
        List<Subpack> subpacks = subpackDao.getAll();
        for (Subpack s : subpacks) {
            System.out.println(s);
        }
        
        System.out.println("Vypisu vsechny packy ");
        List<Pack> packs = packDao.getAll();
        for (Pack p : packs) {
            System.out.println(p);
        }
        
        
        
    }
}
