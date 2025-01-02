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
import model.dao.WordDao;
import model.entities.Word;

public class WordDaoJDBC implements WordDao {
	
	private Connection conn;
	
	public WordDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
    public Long insert(Word obj) {
        Long id = findByWord(obj.getWord());
        try {
            if (id == null) {
                id = insertNewWord(obj);
            } else {
                resetLearnedStatus(id);
                obj.setId(id);
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        	throw new DbException("Error inserting word: " + e.getMessage());
        }
        return id;
    }
	
	private Long insertNewWord(Word obj) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        Long id = null;

        try {
            String sql = "INSERT INTO words (word, learned, created_at) VALUES (?, ?, ?)";
            st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getWord());
            st.setInt(2, 0);
            st.setString(3, LocalDateTime.now().toString());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                    obj.setId(id);
                }
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } finally {
            Database.closeStatement(st);
            Database.closeResultSet(rs);
        }
        return id;
    }
	
	 private void resetLearnedStatus(Long id) throws SQLException {
	        PreparedStatement st = null;
	        try {
	            String sql = "UPDATE words SET learned = 0 WHERE id = ?";
	            st = conn.prepareStatement(sql);

	            st.setLong(1, id);

	            int rowsAffected = st.executeUpdate();

	            if (rowsAffected == 0) {
	            	throw new DbException("Unexpected error! No rows affected when updating 'learned'!");
	            }
	        } finally {
	            Database.closeStatement(st);
	        }
	    }
	
	@Override
	public void update(Long id) {
		PreparedStatement st = null;
	    try {
	        String sql = "UPDATE words SET learned = 1 WHERE id = ?";
	        
	        st = conn.prepareStatement(sql);
	        
	        st.setLong(1, id);
	        
	        int rowsAffected = st.executeUpdate();
	        
	        if (rowsAffected == 0) {
	        	throw new DbException("No rows updated. Word might not exist.");
	        }
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    	throw new DbException("Error while updating word learned status: " + e.getMessage());
	    } finally {
	        Database.closeStatement(st);
	    }
	}

	@Override
	public Word fetchWordToMine() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT id, word FROM words WHERE learned = 0 ORDER BY RANDOM() LIMIT 1";
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			if (rs.next()) {
				Word obj = new Word();
				obj.setId(rs.getLong("id"));
				obj.setWord(rs.getString("word"));
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e.getMessage());
		}
		finally {
			Database.closeStatement(st);
			Database.closeResultSet(rs);
		}
	}

	@Override
	public Long findByWord(String word) {
		Long id = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT id FROM words WHERE word = ?";
	        st = conn.prepareStatement(sql);
	        st.setString(1, word.toLowerCase());
	        rs = st.executeQuery();

	        if (rs.next()) {
	            id = rs.getLong("id");
	        }
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new DbException("Error finding word: " + e.getMessage());
		} 
		finally {
			 Database.closeStatement(st);
		     Database.closeResultSet(rs);
		}
		
		return id;
	}

	@Override
	public List<Word> findAll() {
	    List<Word> words = new ArrayList<>();
	    String sql = "SELECT id, word, learned FROM words";

	    try (PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Long id = rs.getLong("id");
	            String word = rs.getString("word");
	            Integer learned = rs.getInt("learned");
	            words.add(new Word(id, word, learned));
	        }
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    	throw new DbException("Error finding all words: " + e.getMessage()); 
	    }
	    return words;
	}
}
