package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface StatisticsManager {

    double getProgressOfPack(Pack pack) throws DaoException;
    double getProgressOfSubpack(Subpack subpack) throws DaoException;
    double getProgressOfSubpackForPerson(Subpack subpack, Person person) throws DaoException;

    double getCohenKappa(Person person) throws DaoException;
    double getCohenKappa(Person person, Subpack subpack) throws DaoException;

    double averageEvaluationTimeOfSubpack(Subpack subpack) throws DaoException;
    double averageEvaluationTimeOfSubpackForPerson(Subpack subpack, Person person) throws DaoException;

    double averageCompletionTimeOfSubpack(Subpack subpack) throws DaoException;

}
