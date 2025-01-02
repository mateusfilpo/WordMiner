package model.dao;

import java.util.List;

import model.entities.Word;

public interface WordDao {
	
	Long insert(Word word);
	void update(Long id);
	Word fetchWordToMine();
	Long findByWord(String word);
	List<Word> findAll();
}
