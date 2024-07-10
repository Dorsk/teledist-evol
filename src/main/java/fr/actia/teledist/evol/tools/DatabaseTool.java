package fr.actia.teledist.evol.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.actia.teledist.evol.models.GammeData;
import fr.actia.teledist.evol.models.UsineData;

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
    public int executeUpdateQuery(String query, Object... parameters) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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

    public Map<Integer, List<Integer>> getAllJoinGammeUsines() {
        
        Map<Integer, List<Integer>> allGammeByUsine = new HashMap<>();
        String selectGammeQuery = "SELECT idgamme, idusine from joingammeusines";
        ResultSet res = this.executeSelectQuery(selectGammeQuery);
        try {
            while (res.next()) {
                if (allGammeByUsine.get(res.getInt("idusine"))== null){
                    List <Integer> listidGamme= new ArrayList<Integer>();
                    listidGamme.add(res.getInt("idgamme"));
                    allGammeByUsine.put(res.getInt("idusine"), listidGamme);
                } else {
                    List <Integer> listidGamme= allGammeByUsine.get(res.getInt("idusine"));
                    listidGamme.add(res.getInt("idgamme"));
                    allGammeByUsine.put(res.getInt("idusine"), listidGamme);
                }
                //allGammeByUsine.put(res.getInt("idusine"), res.getInt("idgamme"));
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return allGammeByUsine;
    }

    public Map<Integer, UsineData> getAllUsines() {
        Map<Integer, UsineData> allUsinesDataMap = new HashMap<>();

        String selectAllUsinesQuery = "SELECT id, nom, pays from usines";
        ResultSet res = this.executeSelectQuery(selectAllUsinesQuery);
        try {
            while (res.next()) {
                allUsinesDataMap.put(res.getInt("id"), new UsineData(res.getInt("id"), res.getString("nom"), res.getString("pays")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allUsinesDataMap;
    }

    public Map<Integer, GammeData> getAllGammes() {
        Map<Integer, GammeData> allGammesDataMap = new HashMap<>();
        String insertGammeQuery = "SELECT id, nom, vehicule, version, repository from gamme";
        // All gammes
        ResultSet res = this.executeSelectQuery(insertGammeQuery);
        try {
            while (res.next()) {
                GammeData gammeData = new GammeData(res.getInt("id"), res.getString("nom"),
                res.getString("vehicule"), res.getString("version"), res.getString("repository"));
                allGammesDataMap.put(res.getInt("id"), gammeData);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return allGammesDataMap;
    }

    public Map<Integer, UsineData> getAllusinesByidGamme(int idgamme) {

        String insertGammeQuery = "SELECT idusine from joinGammeUsines where idgamme = " + idgamme;
        String selectUsinessQuery = "SELECT id, nom, pays from usines where id IN (";
        String idUsinesList="";
        int countSeparator = 0;

        ResultSet res = this.executeSelectQuery(insertGammeQuery);
        try {
            while (res.next()) {
                if (countSeparator==0){
                    idUsinesList = idUsinesList + Integer.toString(res.getInt("idusine"));
                    countSeparator=1;
                } else {
                    idUsinesList = idUsinesList + "," + Integer.toString(res.getInt("idusine"));
                }
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }

        selectUsinessQuery = selectUsinessQuery + idUsinesList + ")";
        System.err.println(selectUsinessQuery);
        res = this.executeSelectQuery(selectUsinessQuery);
        Map<Integer, UsineData> selectedUsinesDataMap = new HashMap<>();
        try {
            while (res.next()) {
                selectedUsinesDataMap.put(res.getInt("id"), new UsineData(res.getInt("id"), res.getString("nom"), res.getString("pays")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectedUsinesDataMap;
    }

    
}
