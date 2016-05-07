package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.model.Answer;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class AnswerManagerImpl implements AnswerManager {

    @Inject
    private AnswerDao dao;


    @Override
    public Answer getById(Answer answer) {
        return null;
    }




}
