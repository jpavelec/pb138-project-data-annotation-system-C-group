package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface PackManager {

    void createPack(Pack pack, List<Answer> answers, int numOfAnswersInSubpack);

    Pack getPackById(Long id);

    List<Pack> getAllPacks();

    void deletePack(Pack pack);

}
