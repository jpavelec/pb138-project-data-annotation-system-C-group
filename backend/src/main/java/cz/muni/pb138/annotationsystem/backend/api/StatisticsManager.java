package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface StatisticsManager {

    double getProgressOfPack(Pack pack);
    double getProgressOfSubpack(Subpack subpack);
    double getProgressOfSubpackForPerson(Subpack subpack, Person person);

    double getCohenKappa(Person person);
    double getCohenKappa(Person person, Subpack subpack);

    double averageEvaluationTimeOfSubpack(Subpack subpack);
    double averageEvaluationTimeOfSubpackForPerson(Subpack subpack, Person person);

    double averageCompletionTimeOfSubpack(Subpack subpack);

}
