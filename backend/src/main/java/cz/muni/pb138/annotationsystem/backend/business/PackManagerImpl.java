package cz.muni.pb138.annotationsystem.backend.business;

import ch.qos.logback.classic.spi.PackagingDataCalculator;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
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

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

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
            Subpack s = new Subpack(pack, subpackName, new ArrayList<Person>());
            subpackDao.create(s);

            for (int j = 0; j < numOfAnswersInSubpack; j++) {
                int currentAnswer = i*numOfAnswersInSubpack + j;
                if (currentAnswer >= answers.size()) {
                    break;
                }
                Answer a = new Answer(s, answers.get(currentAnswer), false);
                answerDao.create(a);
            }
        }

    }

    @Override
    public Pack getPackById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        return packDao.getById(id);
    }

    @Override
    public List<Pack> getAllPacks() throws DaoException {

        return packDao.getAll();
    }

    @Override
    public void deletePack(Pack pack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (pack.getId() == null || pack.getId() < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        packDao.delete(pack);
    }
}
