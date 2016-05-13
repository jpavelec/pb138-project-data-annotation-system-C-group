package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.EntityNotFoundException;
import cz.muni.pb138.annotationsystem.backend.common.IllegalEntityException;
import cz.muni.pb138.annotationsystem.backend.common.ServiceFailureException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
@Named
public class PackDaoImpl implements Dao<Pack> {
    
    private static final Logger logger = Logger.getLogger(PackDaoImpl.class.getName());
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Inject
    public PackDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void validate(Pack pack) throws IllegalArgumentException {
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (pack.getQuestion() == null) {
            throw new ValidationException("Pack question is null");
        }
        if (pack.getQuestion().isEmpty()) {
            throw new ValidationException("Pack question is empty");
        }
        if (pack.getName() == null) {
            throw new ValidationException("Pack name is null");
        }
        if (pack.getName().isEmpty()) {
            throw new ValidationException("Pack name is empty");
        }
        if (pack.getRepeatingRate() < 0 || pack.getRepeatingRate() > 100) {
            throw new ValidationException("Repeate ratio is out of interval");
        }
        if (pack.getNoiseRate() < 0 || pack.getNoiseRate() > 100) {
            throw new ValidationException("Noise ratio is out of interval");
        }
    }
    
    private Long getKey(ResultSet keyRS, Pack pack) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert pack " + pack
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert pack " + pack
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert pack " + pack
                    + " - no key found");
        }
    }
    
    public static Pack resultSetToPack(ResultSet rs) throws SQLException {
        Pack pack = new Pack();
        pack.setId(rs.getLong("id"));
        pack.setQuestion(rs.getString("question"));
        pack.setName(rs.getString("name"));
        pack.setRepeatingRate(rs.getInt("repeat"));
        pack.setNoiseRate(rs.getInt("noise"));
        return pack;
    }
    
    @Override
    public void create(Pack pack) throws DaoException {
        checkDataSource();
        validate(pack);
        if (pack.getId() != null) {
            throw new IllegalEntityException("Pack id is already set");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO pack (question, name, repeat, noise) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, pack.getQuestion());
            st.setString(2, pack.getName());
            st.setInt(3, (int) pack.getRepeatingRate());
            st.setInt(4, (int) pack.getNoiseRate());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert pack " + pack);
            }

            try (ResultSet keyRS = st.getGeneratedKeys()) {
                pack.setId(getKey(keyRS, pack));
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting pack " + pack, ex);
        }
    }

    @Override
    public Pack getById(Long id) throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, question, name, repeat, noise FROM pack WHERE id = ?")) { 

            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    Pack pack = resultSetToPack(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                            "Internal error: More entities with the same id found " +
                            "(source id: " + id + ", found " + pack + " and " + resultSetToPack(rs));
                    }
                    return pack;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when retrieving pack with id " + id, ex);
        }
    }

    @Override
    public List<Pack> getAll() throws DaoException {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "SELECT id, question, name, repeat, noise FROM pack");
            ResultSet rs = st.executeQuery()) {
            List<Pack> packs = new ArrayList<>();
            while (rs.next()) {
                packs.add(resultSetToPack(rs));
            }
            
            return packs;
            
        } catch (SQLException ex) {
            String msg = "Error when getting all packs from DB";
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void update(Pack pack) throws DaoException {
        checkDataSource();
        validate(pack);
        if (pack.getId() == null) {
            throw new IllegalEntityException("Pack id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "UPDATE pack SET question = ?, name = ?, repeat = ?, noise = ? WHERE id = ?")) {

            st.setString(1, pack.getQuestion());
            st.setString(2, pack.getName());
            st.setInt(3, (int) pack.getRepeatingRate());
            st.setInt(4, (int) pack.getNoiseRate());
            st.setLong(5, pack.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Pack " + pack + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating pack " + pack, ex);
        }
    }

    @Override
    public void delete(Pack pack) throws DaoException {
        checkDataSource();
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (pack.getId() == null) {
            throw new IllegalEntityException("Pack id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM pack WHERE id = ?")) {

            st.setLong(1, pack.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Pack " + pack + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                "Error when updating pack " + pack, ex);
        }
    }

}
