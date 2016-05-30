package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface PackManager {

    void createPack(Pack pack, List<String> answers, List<String> noise, int numOfAnswersInSubpack) throws DaoException;

    Pack getPackById(Long id) throws DaoException;

    List<Pack> getAllPacks() throws DaoException;

    void deletePack(Pack pack) throws DaoException;

}
