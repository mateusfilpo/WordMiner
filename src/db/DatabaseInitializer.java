package db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try {
        	Connection conn = Database.getConnection();
            createWordsTable(conn);
            createMeaningsTable(conn);
        } catch (Exception e) {
        	throw new DbException("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }

    private static void createWordsTable(Connection conn) {
        String sql = """
                CREATE TABLE IF NOT EXISTS words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT NOT NULL UNIQUE,
                    learned INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                );
                """;
        executeSQL(conn, sql, "words");
    }

    private static void createMeaningsTable(Connection conn) {
        String sql = """
                CREATE TABLE IF NOT EXISTS meanings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word_id INTEGER NOT NULL,
                    meaning TEXT NOT NULL,
                    quantity INTEGER NOT NULL DEFAULT 1,
                    learned INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
                );
                """;
        executeSQL(conn, sql, "meanings");
    }

    private static void executeSQL(Connection conn, String sql, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela '" + tableName + "' criada ou já existente.");
        } catch (Exception e) {       	
        	throw new DbException("Erro ao criar a tabela '" + tableName + "': " + e.getMessage());
        }
    }
}
