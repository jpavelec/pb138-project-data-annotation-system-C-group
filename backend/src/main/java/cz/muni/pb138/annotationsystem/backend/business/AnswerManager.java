package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class AnswerManager {

    @Inject
    private AnswerDao dao;

}
