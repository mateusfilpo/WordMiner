package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static Connection conn = null;

    private static final String DATABASE_URL = "jdbc:sqlite:wordmine.db";

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(DATABASE_URL);
                System.out.println("Conexão com o banco de dados SQLite estabelecida.");
            } catch (SQLException e) {
            	throw new DbException("Erro ao conectar ao banco de dados: " + e.getMessage());
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
                System.out.println("Conexão com o banco de dados SQLite fechada.");
            } catch (SQLException e) {
            	throw new DbException("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
            	throw new DbException("Erro ao fechar o Statement: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {            
                throw new DbException("Erro ao fechar o ResultSet: " + e.getMessage());
            }
        }
    }
}
