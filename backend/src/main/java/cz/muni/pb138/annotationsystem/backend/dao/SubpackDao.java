package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class SubpackDao implements Dao<Subpack> {
    
    private DataSource dataSource;

    @Inject
    public SubpackDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Subpack subpack) {
        if (subpack == null) {
            throw new IllegalArgumentException("Subpack is null");
        }
        if (subpack.getParent() == null) {
            throw new ValidationException("Subpack parent is null");
        }
    }

    private Long getKey(ResultSet keyRS, Subpack subpack) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert subpack " + subpack
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert subpack " + subpack
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert subpack " + subpack
                    + " - no key found");
        }
    }
    
    @Override
    public void create(Subpack subpack) throws DaoException {
        checkDataSource();
        validate(subpack);
        if (subpack.getId() != null) {
            throw new ValidationException("Subpack id is already set");
        }

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO subpack (packid, name) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, subpack.getParent().getId());
            st.setString(2, "" /*subpack.getName()*/);
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert subpack " + subpack);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            subpack.setId(getKey(keyRS, subpack));
            
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting subpack " + subpack, ex);
        }
    }
    
    private Subpack resultSetToSubpack(ResultSet rs) throws SQLException, DaoException {
        Subpack subpack = new Subpack();
        subpack.setId(rs.getLong("id"));
        Pack pack = new Pack();
        PackDao packDao = new PackDao(dataSource);
        pack = packDao.getById(rs.getLong("packid"));
        subpack.setParent(pack);
        //subpack.setName(rs.getString("name"));
        return subpack;
    }

    @Override
    public Subpack getById(Long id) throws DaoException {
        checkDataSource();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id, packid, name FROM subpack WHERE id = ?");
            
            st.setLong(1, id);
            
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Subpack subpack = resultSetToSubpack(rs);
                
                if (rs.next()) {
                    throw new ServiceFailureException(
                        "Internal error: More entities with the same id found " +
                        "(source id: " + id + ", found " + subpack + 
                        " and " + resultSetToSubpack(rs));
                }
                return subpack;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retriving subpack with id " + id, ex);
        }
    }

    @Override
    public List<Subpack> getAll() throws DaoException {
        return null;
    }

    @Override
    public void update(Subpack subpack) throws DaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Subpack subpack) throws DaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
