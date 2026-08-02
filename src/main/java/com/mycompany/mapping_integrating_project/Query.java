/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author axelr
 */

public class Query {

    private static int activeUserId = -1;

    public static int getActiveUserId() {
        if (activeUserId <= 0) {
            return 1; 
        }
        return activeUserId;
    }

    public static void setActiveUserId(int activeUserId) {
        Query.activeUserId = activeUserId;
    }
    

    // --- Login validation method ---
    public static boolean validateLogin(String email, String pass) {
        Connection con = Connect.connect();

        if (con == null) {
            System.out.println("Critical error: No connection to the database.");
            return false;
        }

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                boolean success = rs.next();
                if(success) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
            return false;
        }
    }

    public static void registerUser(String name, String email, String pass, String role, String date) {
        Connection con = Connect.connect();

        if (con == null) {
            System.out.println("Critical error: No connection to the database.");
            return;
        }

        String sql = "INSERT INTO users (username, email, password, role, registration_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, role);
            ps.setString(5, date);

            int insertedRows = ps.executeUpdate();

            if (insertedRows > 0) {
                System.out.println("Successful insertion: " + insertedRows + " row inserted");
            } else {
                System.out.println("No rows were inserted");
            }
        } catch (SQLException e) {
            System.out.println("Insertion Error: " + e.getMessage());
        }
    }

    // Method to save land/terrain data calculated by the user
    public static int registerMap(String title, String description, String creationDate, int userId) {
        Connection con = Connect.connect();
        if (con == null) {
            System.out.println("Critical error: No connection to the database.");
            return -1;
        }

        String sql = "INSERT INTO map (title, description, creation_date, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, creationDate);
            ps.setInt(4, userId);

            int insertedRows = ps.executeUpdate();
            if (insertedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        System.out.println("Map inserted successfully with ID: " + rs.getInt(1));
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Map Insertion Error: " + e.getMessage());
        }
        return -1;
    }

    public static boolean registerGeographicElement(String modificationDate, double area, double perimeter, int humidity, double temperature, int layerId, int mapId) {
        String sql = "INSERT INTO geographic_element (modification_date, area, perimeter, humidity, temperature, layer_id, map_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connect.connect()) {
            if (conn == null) {
                System.out.println("Critical error: No connection to the database.");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, modificationDate);
                pstmt.setDouble(2, area);
                pstmt.setDouble(3, perimeter);
                pstmt.setInt(4, humidity);
                pstmt.setDouble(5, temperature);
                pstmt.setInt(6, layerId);
                pstmt.setInt(7, mapId);

                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting geographic element: " + e.getMessage());
            return false;
        }
    }

    /*// Optional method to query registered land records in the console (SELECT with ordering)
    public static void getLandRecords() {
        Connection con = Connect.connect();
        if (con == null) return;

        String sql = "SELECT * FROM land_plots ORDER BY registration_date DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- REGISTERED LAND PLOTS HISTORY ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("land_id") +
                                   " | Alias: " + rs.getString("land_alias") +
                                   " | Perimeter: " + rs.getDouble("perimeter_m") + " m" +
                                   " | Area: " + rs.getDouble("area_m2") + " m²" +
                                   " | Date: " + rs.getTimestamp("registration_date"));
            }
        } catch (SQLException e) {
            System.out.println("Error querying land records: " + e.getMessage());
        }
    }*/
}