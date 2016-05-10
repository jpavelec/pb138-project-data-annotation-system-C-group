package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
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
public class AnswerManagerImpl implements AnswerManager {

    @Inject
    private AnswerDao answerDao;

    @Inject
    private SubpackManager subpackManager;

    @Override
    public Answer nextAnswer(Person person, Subpack subpack) {
        Answer a = new Answer(subpackManager.getSubpackById((long) 1), "Jelen", false);
        a.setId((long) 12);
        return a;
    }

    @Override
    public Answer getAnswerById(Long id) {
        Answer a = new Answer(subpackManager.getSubpackById((long) 1), "Paroháč", false);
        a.setId((long) 10);
        return a;
    }

    @Override
    public List<Answer> getAnswersInSubpack(Subpack subpack) {
        Answer a1 = new Answer(subpackManager.getSubpackById((long) 1), "Paroháč", false);
        a1.setId((long) 10);
        Answer a2 = new Answer(subpackManager.getSubpackById((long) 1), "Jelen", false);
        a2.setId((long) 12);
        List<Answer> a = new ArrayList<>();
        a.add(a1);
        a.add(a2);
        return a;
    }

    @Override
    public List<Answer> getAnswersInPack(Pack pack) {
        Answer a1 = new Answer(subpackManager.getSubpackById((long) 1), "Paroháč", false);
        a1.setId((long) 10);
        Answer a2 = new Answer(subpackManager.getSubpackById((long) 1), "Jelen", false);
        a2.setId((long) 12);
        Answer a3 = new Answer(subpackManager.getSubpackById((long) 1), "Lachtan", true);
        a3.setId((long) 13);
        List<Answer> a = new ArrayList<>();
        a.add(a1);
        a.add(a2);
        a.add(a3);
        return a;
    }
}
