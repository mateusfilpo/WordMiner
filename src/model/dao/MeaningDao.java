package model.dao;

import java.util.List;

import model.entities.Meaning;

public interface MeaningDao {

	void insert(Meaning meaning, Long wordId);
	void update(Long id);
	Meaning fetchMeaningToMine(Long wordId);
	Meaning findByMeaning(String meaning, Long wordId);
	boolean hasExactlyOneUnlearnedMeaning(Long wordId);
	List<Meaning> findAllByWordId(Long wordId);
}
