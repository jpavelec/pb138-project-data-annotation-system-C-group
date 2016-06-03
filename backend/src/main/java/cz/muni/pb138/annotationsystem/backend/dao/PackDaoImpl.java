package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
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
public class PackDaoImpl implements PackDao {
    
    @Inject
    private DataSource dataSource;
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    public PackDaoImpl() {
    }

    public PackDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void validate(Pack pack) {
        if (pack == null) {
            throw new IllegalArgumentException("Pack is null");
        }
        if (pack.getQuestion() == null) {
            throw new ValidationException("Pack question is null");
        }
        if (pack.getQuestion().isEmpty()) {
            throw new ValidationException("Pack question is empty");
        }
        if (pack.getQuestion().length() > 250) {
            throw new ValidationException("Pack question is too long ("+
                    pack.getQuestion().length()+" characters and 250 characters is maximum");
        }
        if (pack.getName() == null) {
            throw new ValidationException("Pack name is null");
        }
        if (pack.getName().isEmpty()) {
            throw new ValidationException("Pack name is empty");
        }
        if (pack.getName().length() > 30) {
            throw new ValidationException("Pack name is too long ("+
                    pack.getName().length()+" characters and 30 characters is maximum");
        }
        if (pack.getRepeatingRate() < 0 || pack.getRepeatingRate() > 100) {
            throw new ValidationException("Repeate ratio is out of interval");
        }
        if (pack.getNoiseRate() < 0 || pack.getNoiseRate() > 100) {
            throw new ValidationException("Noise ratio is out of interval");
        }
        
        Double repeatingRate = pack.getRepeatingRate();
        String[] splitRepeat = repeatingRate.toString().split("\\.");
        if (splitRepeat[1].length() > 2) {
            throw new ValidationException("Decimal part of repeating rate is to long ("
                    + splitRepeat[1].length() + " and 2 is maximum).");
        }
        Double noiseRate = pack.getNoiseRate();
        String[] splitNoise = noiseRate.toString().split("\\.");
        if (splitNoise[1].length() > 2) {
            throw new ValidationException("Decimal part of noise rate is to long ("
                    + splitNoise[1].length() + " and 2 is maximum).");
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
        pack.setRepeatingRate(rs.getDouble("repeat"));
        pack.setNoiseRate(rs.getDouble("noise"));
        return pack;
    }
    
    @Override
    public boolean doesExist(Pack pack) throws DaoException {
        checkDataSource();
        validate(pack);
        if (pack.getId() == null) {
            throw new IllegalArgumentException("Pack id is null");
        }
        if (pack.getId() < 0) {
            throw new IllegalArgumentException("Pack id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id FROM pack WHERE id = ?")) {
            
            st.setLong(1, pack.getId());
            
            try (ResultSet rs = st.executeQuery()) {
            
                if (rs.next()) {
                    if (rs.next()) {
                        throw new DaoException(
                            "Internal error: More entities with the same id found!");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(
                "Error when testing if pack " + pack + " is in DB", ex);
        }
    }
    
    @Override
    public void create(Pack pack) throws DaoException {
        checkDataSource();
        validate(pack);
        if (pack.getId() != null) {
            throw new ValidationException("Pack id is already set");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO pack (question, name, repeat, noise) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, pack.getQuestion());
            st.setString(2, pack.getName());
            st.setDouble(3, pack.getRepeatingRate());
            st.setDouble(4, pack.getNoiseRate());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert pack " + pack);
            }

            try (ResultSet keyRS = st.getGeneratedKeys()) {
                pack.setId(getKey(keyRS, pack));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceFailureException("Error when inserting pack " + pack, ex);
        }
    }

    @Override
    public Pack getById(Long id) throws DaoException, BeanNotExistsException {
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Id is negative");
        }
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
                    String msg = "Pack with id "+id+"was not found in DB";
                    throw new BeanNotExistsException(msg);
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
            throw new ValidationException("Pack id is null");
        }
        if (pack.getId() < 0) {
            throw new ValidationException("Pack id is negative");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "UPDATE pack SET question = ?, name = ?, repeat = ?, noise = ? WHERE id = ?")) {

            st.setString(1, pack.getQuestion());
            st.setString(2, pack.getName());
            st.setDouble(3, pack.getRepeatingRate());
            st.setDouble(4, pack.getNoiseRate());
            st.setLong(5, pack.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                String msg = "Error when updating pack - pack with id " +
                        pack.getId() +" was not found in DB";
                throw new BeanNotExistsException(msg);
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
            throw new ValidationException("Pack id is null");
        }
        try (Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "DELETE FROM pack WHERE id = ?")) {

            st.setLong(1, pack.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new BeanNotExistsException("Pack " + pack + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceFailureException(
                "Error when deleting pack " + pack, ex);
        }
    }

}
