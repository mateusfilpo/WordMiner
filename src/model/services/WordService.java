package model.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import db.DbException;
import model.dao.DaoFactory;
import model.dao.MeaningDao;
import model.dao.WordDao;
import model.entities.Word;

public class WordService {
	
	private WordDao dao = DaoFactory.createWordDao();
	private MeaningDao meaningDao = DaoFactory.createMeaningDao();

	public Word fetchWord() {	
		try {
			Word word = dao.fetchWordToMine();
			if (word == null) {
				Word noWord = new Word();
				noWord.setWord("No words");
				return noWord;
			}
			word.setWord(StringUtils.capitalize(word.getWord()));
			return word;
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Long saveWord(String word) {
		if (word.isEmpty() || word.isBlank()) {
			return null;
		}
		
		Word obj = new Word();
		obj.setWord(word.toLowerCase().trim());
		
		try {
			return dao.insert(obj);
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void markAsLearned(String word, String meaningString) {
		if (word.equals("No Words") && meaningString.equals("No meanings")) {
			return;
		}
		
		try {
			Long wordId = findWordIdByWord(word);
			
			if (wordId != null) {
				if (wordId != null && meaningDao.hasExactlyOneUnlearnedMeaning(wordId)) {
					dao.update(wordId);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Long findWordIdByWord(String word) {
		try {
			return dao.findByWord(word);
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<Word> findAllWords() {
		try {
			return dao.findAll();
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
