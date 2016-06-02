package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.EvaluationDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class AnswerManagerImpl implements AnswerManager {

    @Inject
    private AnswerDao answerDao;

    @Inject
    private SubpackManager subpackManager;

    @Inject
    private EvaluationManager evaluationManager;

    @Override
    @Transactional
    public Answer nextAnswer(Person person, Subpack subpack) throws DaoException {
        if (person == null) {
            throw new IllegalArgumentException("person is null");
        }
        if (subpack == null) {
            throw new IllegalArgumentException("subpack is null");
        }
        if (!subpackManager.getSubpacksAssignedToPerson(person).contains(subpack)) {
            throw new IllegalStateException("Person is not assigned to subpack");
        }

        // TODO definitely create some Dao method. e.g. getUnevaluatedAnswers(person, subpack)
        List<Evaluation> evaluations = evaluationManager.getEvaluationsOfPerson(person);
        for (Answer a : this.getAnswersInSubpack(subpack)) {
            boolean isEvaluated = false;
            for (Evaluation e : evaluations) {
                if (e.getAnswer().equals(a)) {
                    isEvaluated = true;
                }
            }
            if (!isEvaluated) {
                return a;
            }
        }
        throw new IllegalStateException("No more answers left.");
    }

    @Override
    @Transactional
    public Answer getAnswerById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id is null or negative");
        }

        return answerDao.getById(id);
    }

    @Override
    @Transactional
    public List<Answer> getAnswersInSubpack(Subpack subpack) throws DaoException {
        if (subpack == null) {
            throw new IllegalArgumentException("subpack is null");
        }

        List<Answer> answers = new ArrayList<>();
        for (Answer a : answerDao.getAll()) {
            if (a.getFromSubpack().equals(subpack)) {
                answers.add(a);
            }
        }
        return answers;
    }

    @Override
    @Transactional
    public List<Answer> getAnswersInPack(Pack pack) throws DaoException {
        if (pack == null) {
            throw new IllegalArgumentException("pack is null");
        }

        Set<Answer> answers = new HashSet<>();
        answerLoop: for (Answer a : answerDao.getAll()) {
            if (a.getFromSubpack().getParent().equals(pack)) {
                if (!a.isIsNoise()) {
                    for (Answer r : answers) {
                        if (r.getAnswer().equals(a.getAnswer())) {
                            continue answerLoop;
                        }
                    }
                    answers.add(a);
                }
            }
        }
        return new ArrayList<>(answers);
    }
}
