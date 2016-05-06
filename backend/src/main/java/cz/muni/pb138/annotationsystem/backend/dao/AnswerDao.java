package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Answer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class AnswerDao implements Dao<Answer> {

    private JdbcTemplate jdbc;

    @Inject
    public void setDataSource(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(Answer bean) {

    }

    @Override
    public Answer getById(Long id) {
        Answer a = new Answer();
        a.setAnswer(String.valueOf(id));
        return a;
    }

    @Override
    public void update(Answer bean) {

    }

    @Override
    public void delete(Answer bean) {

    }

}
