package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public interface AnswerManager {

    Answer getById(Answer answer);

}
