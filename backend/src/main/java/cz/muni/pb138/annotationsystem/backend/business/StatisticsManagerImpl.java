package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.StatisticsManager;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class StatisticsManagerImpl implements StatisticsManager {

    @Override
    @Transactional
    public double getProgressOfPack(Pack pack) {
        return 0.5;
    }

    @Override
    @Transactional
    public double getProgressOfSubpack(Subpack subpack) {
        return 0.2;
    }

    @Override
    @Transactional
    public double getProgressOfSubpackForPerson(Subpack subpack, Person person) {
        return 0.6;
    }

    @Override
    @Transactional
    public double getProgressOfPerson(Person person) {
        return 0.4;
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
    public double averageEvaluationTimeOfSubpack(Subpack subpack) {
        return 44;
    }

    @Override
    @Transactional
    public double averageEvaluationTimeForPerson(Person person) {
        return 30;
    }

    @Override
    @Transactional
    public double averageEvaluationTimeOfSubpackForPerson(Subpack subpack, Person person) {
        return 60;
    }

    @Override
    @Transactional
    public double averageCompletionTimeOfSubpack(Subpack subpack) {
        return 24563725;
    }

    @Override
    @Transactional
    public double averageCompletionTimeForPerson(Person person) {
        return 28563725;
    }
}
