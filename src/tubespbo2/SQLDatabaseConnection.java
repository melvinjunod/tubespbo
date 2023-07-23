/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tubespbo2;
import java.sql.*;
import java.util.Arrays;

public class SQLDatabaseConnection {
    static String connectionUrl;
    static String user;
    static String password;
    static ResultSet resultSet;
    public Object[][] returnedRows = new Object[10][10];
    static int rowCount = 0;
    
    public SQLDatabaseConnection(String query, String type, int columnNum) throws SQLException {
        rowCount = 0;
        connectionUrl = "jdbc:mysql://localhost:3307/databasermmotor";
        user = "staff";
        password = "rmMotorBandung";
        resultSet = null;
        try (Connection connection = DriverManager.getConnection(connectionUrl, user, password)) {
            Statement statement;
            statement = connection.createStatement();

            if("select".equals(type)) {            
                // Create and execute a SELECT SQL statement.
                String selectSql = query;
                resultSet = statement.executeQuery(selectSql);


                while (resultSet.next()) {
                    for(var i = 0; i < columnNum; i++) {
                        if(resultSet.getString(i+1) == null) {
                            returnedRows[rowCount][i] = "(Tidak ada data)";
                        }
                        else {
                            returnedRows[rowCount][i] = resultSet.getString(i+1);
                        }
                    }
                    rowCount++;
                }
            }
            else if("update".equals(type)) {
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.executeUpdate();
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}