package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import db.Database;
import db.DbException;
import model.dao.MeaningDao;
import model.entities.Meaning;

public class MeaningDaoJDBC implements MeaningDao {

    private Connection conn;

    public MeaningDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Meaning obj, Long wordId) {
        try {
            Meaning existingMeaning = findByMeaning(obj.getMeaning(), wordId);
            if (existingMeaning == null) {
                insertNewMeaning(obj, wordId);
            } else {
                incrementMeaningQuantity(existingMeaning);
                obj.setId(existingMeaning.getId());
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        	throw new DbException("Error inserting meaning: " + e.getMessage());
        }
    }
    
    private void insertNewMeaning(Meaning obj, Long wordId) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO meanings (word_id, meaning, learned, created_at, quantity) VALUES (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            st.setLong(1, wordId);
            st.setString(2, obj.getMeaning().toLowerCase());
            st.setInt(3, 0);
            st.setString(4, LocalDateTime.now().toString());
            st.setInt(5, 1);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    obj.setId(id);
                }
            } else {
            	throw new DbException("Unexpected error! No rows affected!");
            }
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }
    
    private void incrementMeaningQuantity(Meaning existingMeaning) throws SQLException {
        PreparedStatement st = null;
        try {
            String sql = "UPDATE meanings SET quantity = quantity + 1, learned = CASE WHEN quantity = 0 THEN 0 ELSE learned END, created_at = ? WHERE id = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, LocalDateTime.now().toString());
            st.setLong(2, existingMeaning.getId());
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
            	throw new DbException("Unexpected error! No rows affected when updating meaning!");
            }
        } finally {
            Database.closeStatement(st);
        }
    }

    @Override
    public void update(Long id) {
        try {
            conn.setAutoCommit(false);
            Meaning meaning = findMeaningById(id);
            if (meaning != null) {
                if (meaning.getQuantity() > 0) {
                    decrementQuantity(id, meaning.getQuantity());
                } else {
                    System.out.println("Meaning with quantity <= 0 found. ID: " + id);
                }
            } else {
            	throw new DbException("Meaning not found with ID: " + id);
            }
            conn.commit();
        } catch (SQLException e) {
        	e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
            	ex.printStackTrace();
                throw new DbException("Error during rollback: " + ex.getMessage());
            }         
            throw new DbException("Error updating meaning: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
            	e.printStackTrace();
            	throw new DbException("Error restoring autocommit: " + e.getMessage());
            }
        }
    }
    
    private Meaning findMeaningById(Long id) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String selectSql = "SELECT quantity, learned, meaning FROM meanings WHERE id = ?";
            st = conn.prepareStatement(selectSql);
            st.setLong(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                int learned = rs.getInt("learned");
                String meaningValue = rs.getString("meaning");
                Meaning meaning = new Meaning();
                meaning.setQuantity(quantity);
                meaning.setLearned(learned);
                meaning.setMeaning(meaningValue);
                return meaning;
            } else {
                return null;
            }
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }
    
    private void decrementQuantity(Long id, int currentQuantity) throws SQLException {
        PreparedStatement st = null;
        try {
            String updateSql = "UPDATE meanings SET quantity = ?, learned = ? WHERE id = ?";
            st = conn.prepareStatement(updateSql);
            st.setInt(1, currentQuantity - 1);
            st.setInt(2, currentQuantity == 1 ? 1 : 0);
            st.setLong(3, id);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected == 0) {           	
            	throw new DbException("Error updating meaning quantity.");
            }
        } finally {
            Database.closeStatement(st);
        }
    }
    
    @Override
    public Meaning fetchMeaningToMine(Long wordId) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT id, meaning, quantity FROM meanings WHERE learned = 0 AND word_id = ? ORDER BY created_at ASC LIMIT 1";
            st = conn.prepareStatement(sql);
            st.setLong(1, wordId);
            rs = st.executeQuery();
            if (rs.next()) {
                Meaning obj = new Meaning();
                obj.setId(rs.getLong("Id"));
                obj.setMeaning(rs.getString("Meaning"));
                obj.setQuantity(rs.getInt("quantity"));
                return obj;
            }
            Meaning empty = new Meaning();
            empty.setId(-1L);
            empty.setMeaning("General Meaning");
            return empty;
        } catch (SQLException e) {
        	e.printStackTrace();
        	throw new DbException(e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }

    @Override
    public Meaning findByMeaning(String meaning, Long wordId) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, meaning, quantity FROM meanings WHERE meaning = ? AND word_id = ? LIMIT 1";
            st = conn.prepareStatement(sql);
            st.setString(1, meaning.toLowerCase());
            st.setLong(2, wordId);
            rs = st.executeQuery();

            if (rs.next()) {
                Meaning obj = new Meaning();
                obj.setId(rs.getLong("Id"));
                obj.setMeaning(rs.getString("Meaning"));
                obj.setQuantity(rs.getInt("Quantity"));
                return obj;
            }
        } catch (SQLException e) {     	
        	e.printStackTrace();
        	throw new DbException("Error finding meaning: " + e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
		return null;
    }

    @Override
    public boolean hasExactlyOneUnlearnedMeaning(Long wordId) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
        	String sql = "SELECT COUNT(*) as count, MAX(quantity) as maxQuantity " +
                    "FROM meanings " +
                    "WHERE word_id = ? AND learned = 0";
        	st = conn.prepareStatement(sql);

            st.setLong(1, wordId);

            rs = st.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                int maxQuantity = rs.getInt("maxQuantity");

                return count == 1 && maxQuantity == 1;
            }
            return false;
        } catch (SQLException e) {
        	e.printStackTrace();
        	throw new DbException("Error checking unlearned meanings for wordId: " + wordId + ": " + e.getMessage());
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
    }

	@Override
	public List<Meaning> findAllByWordId(Long wordId) {
		List<Meaning> meanings = new ArrayList<>();
	    String sql = "SELECT id, meaning, learned FROM meanings WHERE word_id = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setLong(1, wordId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Long id = rs.getLong("id");
	                String meaning = rs.getString("meaning");
	                Integer learned = rs.getInt("learned");
	                meanings.add(new Meaning(id, meaning, learned));
	            }
	        }
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    	throw new DbException("Error finding meanings by word id: " + e.getMessage());
	    }
	    return meanings;
	}
}