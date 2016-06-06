package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.api.StatisticsManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.dao.PackDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDao;
import cz.muni.pb138.annotationsystem.backend.dao.SubpackDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class StatisticsManagerImpl implements StatisticsManager {

    @Inject
    private EvaluationDao evaluationDao;
    @Inject
    private SubpackManager subpackManager;
    @Inject
    private AnswerManager answerManager;
    @Inject
    private PackDao packDao;
    @Inject
    private SubpackDao subpackDao;
    @Inject
    private PersonDao personDao;

    @Override
    @Transactional
    public double getProgressOfPack(Pack pack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("given pack is null");
        }
        if (pack.getId() == null || pack.getId() < 0) {
            throw new ValidationException("given pack id is null or negative");
        }
        if (!packDao.doesExist(pack)) {
            throw new BeanNotExistsException("given pack does not exist");
        }

        int total = 0;
        for (Subpack subpack : subpackManager.getSubpacksInPack(pack)) {
            total += answerManager.getAnswersInSubpack(subpack).size() * subpackManager.getPersonsAssignedToSubpack(subpack).size();
        }

        int evaluated = 0;
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getAnswer().getFromSubpack().getParent().equals(pack)) {
                evaluated ++;
            }
        }

        return 100.0*evaluated/total;
    }

    @Override
    @Transactional
    public double getProgressOfSubpack(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("given subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("given subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }

        int total = answerManager.getAnswersInSubpack(subpack).size() * subpackManager.getPersonsAssignedToSubpack(subpack).size();

        int evaluated = 0;
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getAnswer().getFromSubpack().equals(subpack)) {
                evaluated ++;
            }
        }

        return 100.0*evaluated/total;
    }

    @Override
    @Transactional
    public double getProgressOfSubpackForPerson(Subpack subpack, Person person) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("given subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("given subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }
        if (person == null) {
            throw new IllegalArgumentException("given person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new ValidationException("given person id is null or negative");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("given person does not exist");
        }
        if (!subpackManager.getPersonsAssignedToSubpack(subpack).contains(person)) {
            throw new IllegalStateException("person "+person.getUsername()+" is not assigned to subpack "+subpack);
        }


        int total = answerManager.getAnswersInSubpack(subpack).size();

        int evaluated = 0;
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getAnswer().getFromSubpack().equals(subpack)) {
                if (evaluation.getPerson().equals(person)) {
                    evaluated ++;
                }
            }
        }

        return 100.0*evaluated/total;
    }

    @Override
    @Transactional
    public Double getCohenKappa(Person person) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("given person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new ValidationException("given person id is null or negative");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("given person does not exist");
        }

        return getCohenKappaInternal(person, null);
    }

    @Override
    @Transactional
    public Double getCohenKappa(Person person, Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("given subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("given subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }
        if (person == null) {
            throw new IllegalArgumentException("given person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new ValidationException("given person id is null or negative");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("given person does not exist");
        }
        if (!subpackManager.getPersonsAssignedToSubpack(subpack).contains(person)) {
            throw new IllegalStateException("person "+person.getUsername()+" is not assigned to subpack "+subpack);
        }

        return getCohenKappaInternal(person, subpack);
    }

    private Double getCohenKappaInternal(Person person, Subpack subpack) throws DaoException {

        int pospos = 0;
        int posneg = 0;
        int negpos = 0;
        int negneg = 0;

        Map<Answer, Evaluation> aux = new HashMap<>();
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getRating().equals(Rating.NONSENSE)) {
                continue;
            }
            if (subpack == null || evaluation.getAnswer().getFromSubpack().equals(subpack)) {

                if (evaluation.getPerson().equals(person)) {
                    if (evaluation.getAnswer().isIsNoise()) {
                        if (evaluation.getRating().equals(Rating.POSITIVE)) {
                            negpos ++;
                        } else if (evaluation.getRating().equals(Rating.NEGATIVE)) {
                            negneg ++;
                        }
                    } else {
                        if (aux.containsKey(evaluation.getAnswer())) {
                            if (aux.get(evaluation.getAnswer()).getRating().equals(Rating.POSITIVE)) {
                                if (evaluation.getRating().equals(Rating.POSITIVE)) {
                                    pospos ++;
                                } else if (evaluation.getRating().equals(Rating.NEGATIVE)) {
                                    posneg ++;
                                }
                            } else if (aux.get(evaluation.getAnswer()).getRating().equals(Rating.NEGATIVE)) {
                                if (evaluation.getRating().equals(Rating.POSITIVE)) {
                                    negpos ++;
                                } else if (evaluation.getRating().equals(Rating.NEGATIVE)) {
                                    negneg ++;
                                }
                            }
                            aux.remove(evaluation.getAnswer());
                        } else {
                            aux.put(evaluation.getAnswer(), evaluation);
                        }
                    }
                }
            }
        }

        int total = pospos + posneg + negpos + negneg;
        if (total == 0) {
            return null;
        }
        double prsta = 1.0*(pospos+negneg)/total;
        double prste = 1.0*((pospos+posneg)*(pospos+negpos))/(total*total) + 1.0*((negneg+negpos)*(negneg+posneg))/(total*total);

        return (prsta - prste)/(1 - prste);
    }

    @Override
    @Transactional
    public Double averageEvaluationTimeOfSubpack(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("given subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("given subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }

        int sum = 0;
        int total = 0;
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getAnswer().getFromSubpack().equals(subpack)) {
                sum += evaluation.getElapsedTime();
                total ++;
            }
        }

        if (total == 0) {
            return null;
        }
        return 1.0*sum/total;
    }

    @Override
    @Transactional
    public Double averageEvaluationTimeOfSubpackForPerson(Subpack subpack, Person person) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("given subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("given subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }
        if (person == null) {
            throw new IllegalArgumentException("given person is null");
        }
        if (person.getId() == null || person.getId() < 0) {
            throw new ValidationException("given person id is null or negative");
        }
        if (!personDao.doesExist(person)) {
            throw new BeanNotExistsException("given person does not exist");
        }
        if (!subpackManager.getPersonsAssignedToSubpack(subpack).contains(person)) {
            throw new IllegalStateException("person "+person.getUsername()+" is not assigned to subpack "+subpack);
        }

        int sum = 0;
        int total = 0;
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getAnswer().getFromSubpack().equals(subpack)) {
                if (evaluation.getPerson().equals(person)) {
                    sum += evaluation.getElapsedTime();
                    total ++;
                }
            }
        }

        if (total == 0) {
            return null;
        }
        return 1.0*sum/total;
    }

    @Override
    @Transactional
    public Double averageCompletionTimeOfSubpack(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("given subpack is null");
        }
        if (subpack.getId() == null || subpack.getId() < 0) {
            throw new ValidationException("given subpack id is null or negative");
        }
        if (!subpackDao.doesExist(subpack)) {
            throw new BeanNotExistsException("given subpack does not exist");
        }
        List<Person> usersOnSubpack = subpackDao.getPeopleAssignedToSubpack(subpack);
        long sumOfTimes = 0;
        for (Person p : usersOnSubpack) {
            if (subpackDao.getCompletationTime(subpack, p) == null) {
                return null;
            }
            sumOfTimes += subpackDao.getAssignationTime(subpack, p) - 
                          subpackDao.getCompletationTime(subpack, p);
        }
        

        return  1.0*sumOfTimes/usersOnSubpack.size();
    }

}
