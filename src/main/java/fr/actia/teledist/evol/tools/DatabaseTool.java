package fr.actia.teledist.evol.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTool {

    private static final String URL = "jdbc:postgresql://localhost:5432/teledistribution";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    private Connection connection;

    public DatabaseTool() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection to PostgreSQL has been established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Select query
    public ResultSet executeSelectQuery(String query) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update query
    public int executeUpdateQuery(String query) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Delete query
    public int executeDeleteQuery(String query) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Close the connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to PostgreSQL has been closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert query
    public int executeInsertQuery(String query, Object... parameters) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // main Tests 
    public static void main(String[] args) {
        DatabaseTool dbUtility = new DatabaseTool();
        
        // Example usage
        String selectQuery = "SELECT * FROM your_table";
        ResultSet resultSet = dbUtility.executeSelectQuery(selectQuery);
        try {
            while (resultSet.next()) {
                System.out.println("Column 1: " + resultSet.getString(1));
                System.out.println("Column 2: " + resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String updateQuery = "UPDATE your_table SET column1 = 'new_value' WHERE column2 = 'some_value'";
        int rowsAffected = dbUtility.executeUpdateQuery(updateQuery);
        System.out.println("Rows affected by update: " + rowsAffected);

        String deleteQuery = "DELETE FROM your_table WHERE column1 = 'some_value'";
        rowsAffected = dbUtility.executeDeleteQuery(deleteQuery);
        System.out.println("Rows affected by delete: " + rowsAffected);

        dbUtility.closeConnection();
    }

    
}
