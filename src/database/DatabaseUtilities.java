package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oracle.jdbc.driver.OracleDriver;

class DatabaseUtilities {

    // Database Connections
    private static final String DB_URL = "jdbc:oracle:thin:@127.1.1.0:1521:xe";
    private static final String DB_USER = "chat";
    private static final String DB_PASSWORD = "chat";

    // Database Utilities
    private static final DatabaseUtilities databaseUtilities = new DatabaseUtilities();

    // Get the Database Utilities instance
    public static DatabaseUtilities getDatabaseUtilities() {
        return databaseUtilities;
    }

    // Database Connection
    private Connection connection;

    // Constructors
    private DatabaseUtilities() {
        try {
            DriverManager.registerDriver(new OracleDriver());
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Select Records
    public ResultSet select(String table) throws SQLException {
        return select(table, null, null);
    }

    public ResultSet select(String table, String[] columns) throws SQLException {
        return select(table, columns, null);
    }

    public ResultSet select(String table, String condition) throws SQLException {
        return select(table, null, condition);
    }

    public ResultSet select(String table, String[] columns, String condition) throws SQLException {
        String query = "SELECT ";
        if (columns != null && columns.length != 0) {
            for (int i = 0; i < columns.length; i++) {
                query += columns[i];
                if (i != columns.length - 1) {
                    query += ", ";
                }
            }
        } else {
            query += "*";
        }
        query += " FROM " + table;
        if (condition != null) {
            query += " WHERE " + condition;
        }

        return connection.createStatement().executeQuery(query);
    }

    // Insert Record
    public void insert(String table, String[] values) throws SQLException {
        insert(table, null, values);
    }

    public void insert(String table, String[] columns, String[] values) throws SQLException {
        String query = "INSERT INTO " + table;
        if (columns != null && columns.length != 0) {
            query += " (";
            for (int i = 0; i < columns.length; i++) {
                query += columns[i];
                if (i != columns.length - 1) {
                    query += ", ";
                }
            }
            query += ")";
        }
        query += " VALUES (";
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                query += "null";
            } else {
                query += values[i];
            }
            if (i != values.length - 1) {
                query += ", ";
            }
        }
        query += ")";

        Statement statement = connection.createStatement();
        statement.executeQuery(query);
        statement.close();
    }

    // Update Record
    public void update(String table, String[] columns, String[] values, String condition) throws SQLException {
        String query = "UPDATE " + table + " SET ";
        for (int i = 0; i < columns.length; i++) {
            query += columns[i] + "=";
            if (values[i] == null) {
                query += "null";
            } else {
                query += values[i];
            }
            if (i != values.length - 1) {
                query += ", ";
            }
        }
        query += " WHERE " + condition;

        Statement statement = connection.createStatement();
        statement.executeQuery(query);
        statement.close();
    }

    // Delete Record
    public void delete(String table, String condition) throws SQLException {
        String query = "DELETE FROM " + table + " WHERE " + condition;
        Statement statement = connection.createStatement();
        statement.executeQuery(query);
        statement.close();
    }

    // Print ResultSet
    public void printResultSet(ResultSet resultSet, String[] columns) {
        try {
            while (resultSet.next()) {
                for (int i = 0; i < columns.length; i++) {
                    System.out.print(resultSet.getString(columns[i]));
                    if (i != columns.length - 1) {
                        System.out.print(" : ");
                    }
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Commit
    public void commit() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("COMMIT");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Roll Back
    public void rollBack() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("ROLLBACK");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
