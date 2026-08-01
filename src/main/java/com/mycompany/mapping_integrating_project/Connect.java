/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

/**
 * 
 * @author axelr
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private static final String URL = "jdbc:mysql://localhost:3306/mapeo_proyecto";
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // Make sure to place your real password here
    
    private static Connection singleConnection;

    private Connect() {} 

    public static synchronized Connection connect() {
        try {
            // Force in-memory loading of the MySQL Driver (Technique required for QA audit)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (singleConnection == null || singleConnection.isClosed()) {
                singleConnection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Successful Singleton connection to Map.tre");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: MySQL Driver not found in Maven dependencies.");
        } catch (SQLException e) {
            System.out.println("Database ERROR (Check credentials or if the server is running): " + e.getMessage());
        }
        return singleConnection;
    }
}