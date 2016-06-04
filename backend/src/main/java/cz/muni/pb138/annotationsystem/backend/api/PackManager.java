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

    /**
     * Generate new pack with all needed data. Create pack, Create subpacks according to numOfAnswersInSubpack.
     * Last subpack has less than number of answers.
     * create answers from given list of answers and divide them into packs, generate some answers twice (or more times)
     * according to repeating factor in pack, generate noise into subpack according to given noise factor in pack.
     *
     * @param pack Created pack. Contains repeatign factor and noise factor
     * @param answers answers to be created
     * @param noise noise to be added to generated subpacks
     * @param numOfAnswersInSubpack number of answers in one subpack
     * @throws DaoException
     */
    void createPack(Pack pack, List<String> answers, List<String> noise, int numOfAnswersInSubpack) throws DaoException;

    /**
     * @return pack with given id
     * @param id Id of requested pack
     * @throws DaoException
     */
    Pack getPackById(Long id) throws DaoException;

    /**
     * @return All pack in the system
     * @throws DaoException
     */
    List<Pack> getAllPacks() throws DaoException;

    /**
     * Delete given pack
     *
     * @param pack pack to be deleted
     * @throws DaoException
     */
    void deletePack(Pack pack) throws DaoException;

}
