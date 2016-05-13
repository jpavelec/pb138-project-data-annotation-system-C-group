package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class Main {
    
    private static final int PACK_SIZE = 20;
    
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
    }
}
