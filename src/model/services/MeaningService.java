package model.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import db.DbException;
import model.dao.DaoFactory;
import model.dao.MeaningDao;
import model.entities.Meaning;

public class MeaningService {
	
	private MeaningDao dao = DaoFactory.createMeaningDao();

	public void saveMeaning(String meaning, Long wordId) {
		if (wordId == null) {
			return;
		}
		Meaning obj = new Meaning();
		if (meaning.isEmpty())
			obj.setMeaning("General Meaning");
		else
			obj.setMeaning(meaning.toLowerCase());
		try {
			dao.insert(obj, wordId);
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Meaning fetchMeaning(Long wordId) {
		if (wordId == null) {
			Meaning noMeaning = new Meaning();
			noMeaning.setMeaning("No meanings");
			return noMeaning;
		}
		
		try {
			Meaning meaning = dao.fetchMeaningToMine(wordId);
			meaning.setMeaning(StringUtils.capitalize(meaning.getMeaning()));
			return meaning;
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void markAsLearned(String meaning, Long wordId) {
		if (wordId == null) {
			return;
		}
		
		try {
			Meaning existMeaning = dao.findByMeaning(meaning, wordId);
			
			if (existMeaning != null) {
				dao.update(existMeaning.getId());
			}
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<Meaning> findAllMeaningsByWordId(Long wordId) {
		try {
			return dao.findAllByWordId(wordId);
		} catch (DbException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
