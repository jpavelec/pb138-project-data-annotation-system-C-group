package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Answer;

import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class AnswerDao implements Dao<Answer> {

    @Override
    public void create(Answer bean) {

    }

    @Override
    public Answer getById(Long id) {
        return null;
    }

    @Override
    public void update(Answer bean) {

    }

    @Override
    public void delete(Answer bean) {

    }

}
