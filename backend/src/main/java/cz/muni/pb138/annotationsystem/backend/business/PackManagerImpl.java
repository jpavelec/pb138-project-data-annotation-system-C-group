package cz.muni.pb138.annotationsystem.backend.business;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.PackDao;
import cz.muni.pb138.annotationsystem.backend.dao.PackDaoImpl;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class PackManagerImpl implements PackManager {

    @Inject
    private PackDao packDao;

    @Inject
    private SubpackDao subpackDao;

    @Inject
    private AnswerDao answerDao;

    @Override
    @Transactional
    public void createPack(Pack pack, List<String> answers, List<String> noise, int numOfAnswersInSubpack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("List of answers is null or empty");
        }
        if (noise == null) {
            throw new IllegalArgumentException("Noise is null. Pass empty list if you do not want to set noise.");
        }
        if (numOfAnswersInSubpack <= 0) {
            throw new IllegalArgumentException("Number of answers in subpack is negative");
        }
        if (pack.getId() != null) {
            throw new ValidationException("Created pack already has id");
        }
        if (pack.getName() == null || pack.getName().isEmpty()) {
            throw new ValidationException("Created pack name is null or empty");
        }
        if (pack.getNoiseRate() < 0) {
            throw new ValidationException("noise rate is negative");
        }
        if (pack.getNoiseRate() > 0 && noise.isEmpty()) {
            throw new ValidationException("noise rate positive however no noise has been passed");
        }
        if (pack.getQuestion() == null || pack.getQuestion().isEmpty()) {
            throw new ValidationException("Question is null or empty");
        }
        if (pack.getRepeatingRate() < 0) {
            throw new ValidationException("Repeating rate negative");
        }
        for (String a : answers) {
            if (a == null || a.isEmpty()) {
                throw new ValidationException("Some answer in list is null or empty");
            }
        }
        for (String n : noise) {
            if (n == null || n.isEmpty()) {
                throw new ValidationException("Some noise in list is null or empty");
            }
        }

        packDao.create(pack);

        int numOfSubpacks = (int) Math.ceil(((double)answers.size())/((double)numOfAnswersInSubpack));


        for (int i = 0; i < numOfSubpacks; i++) {


            String subpackName = String.format("%0"+String.valueOf(numOfSubpacks).length()+"d", i);
            Subpack subpack = new Subpack(pack, subpackName);
            subpackDao.create(subpack);


            int currentNumOfAnswersInSubpack = Math.min(numOfAnswersInSubpack, answers.size()-i*numOfAnswersInSubpack);
            List<String> answersInSubpack = answers.subList(i*numOfAnswersInSubpack, i*numOfAnswersInSubpack + currentNumOfAnswersInSubpack);
            List<Answer> answersInSubpackCreated = new LinkedList<>();
            for (String answer : answersInSubpack) {

                Answer a = new Answer(subpack, answer, false);
                answerDao.create(a);
                answersInSubpackCreated.add(a);
            }

            if (pack.getNoiseRate() > 0) {
                int expectedNumOfNoise = (int) Math.ceil(currentNumOfAnswersInSubpack*(pack.getNoiseRate()/100));

                int currentNumOfNoise = 0;
                do {
                    Collections.shuffle(noise);
                    for (String grain : noise) {
                        if (currentNumOfNoise >= expectedNumOfNoise) {
                            break;
                        }
                        Answer g = new Answer(subpack, grain, true);
                        answerDao.create(g);
                        currentNumOfNoise ++;
                    }
                } while (currentNumOfNoise < expectedNumOfNoise);
            }


            if (pack.getRepeatingRate() > 0) {
                int expectedNumOfRepeating = (int) Math.ceil(currentNumOfAnswersInSubpack*(pack.getRepeatingRate()/100));

                int currentNumOfRepeating = 0;
                do {
                    Collections.shuffle(answersInSubpackCreated);
                    for (Answer repeating : answersInSubpackCreated) {
                        if (currentNumOfRepeating >= expectedNumOfRepeating) {
                            break;
                        }
                        answerDao.createRepeatingAnswer(repeating);
                        currentNumOfRepeating ++;
                    }
                } while (currentNumOfRepeating < expectedNumOfRepeating);
            }

        }

    }

    @Override
    @Transactional
    public Pack getPackById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        return packDao.getById(id);
    }

    @Override
    @Transactional
    public List<Pack> getAllPacks() throws DaoException {

        return packDao.getAll();
    }

    @Override
    @Transactional
    public void deletePack(Pack pack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (pack.getId() == null || pack.getId() < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }
        if (!packDao.doesExist(pack)) {
            throw new BeanNotExistsException("Given pack does not exists");
        }

        packDao.delete(pack);
    }
}
