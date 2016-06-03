package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.api.StatisticsManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

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
    private EvaluationManager evaluationManager;

    @Override
    @Transactional
    public double getProgressOfPack(Pack pack) throws DaoException {
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
        int total = answerManager.getAnswersInSubpack(subpack).size() * subpackManager.getPersonsAssignedToSubpack(subpack).size();

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
    public double getCohenKappa(Person person) {
        return 0.8;
    }

    @Override
    @Transactional
    public double getCohenKappa(Person person, Subpack subpack) {
        return 0.7;
    }

    @Override
    @Transactional
    public double averageEvaluationTimeOfSubpack(Subpack subpack) throws DaoException {

        int sum = 0;
        int total = 0;
        for (Evaluation evaluation : evaluationDao.getAll()) {
            if (evaluation.getAnswer().getFromSubpack().equals(subpack)) {
                sum += evaluation.getElapsedTime();
                total ++;
            }
        }

        return 1.0*sum/total;
    }

    @Override
    @Transactional
    public double averageEvaluationTimeOfSubpackForPerson(Subpack subpack, Person person) throws DaoException {

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

        return 1.0*sum/total;
    }

    @Override
    @Transactional
    public double averageCompletionTimeOfSubpack(Subpack subpack) {
        return 24563725;
    }

}
