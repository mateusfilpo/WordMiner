package model.dao;

import db.Database;
import model.dao.impl.MeaningDaoJDBC;
import model.dao.impl.WordDaoJDBC;

public class DaoFactory {
	
	public static WordDao createWordDao() {
		return new WordDaoJDBC(Database.getConnection());
	}
	
	public static MeaningDao createMeaningDao() {
		return new MeaningDaoJDBC(Database.getConnection());
	}

}
