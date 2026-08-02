/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author axelr
 */

public class Query {

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
    
    public static ResultSet getUserData(String email, String pass) {
    Connection con = Connect.connect();
    if (con == null) return null;

    String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
    try {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);
        ps.setString(2, pass);
        return ps.executeQuery();
    } catch (SQLException e) {
        System.out.println("Error retrieving user data: " + e.getMessage());
        return null;
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
    public static boolean saveLand(String alias, double perimeter, double area) {
        Connection con = Connect.connect();
        if (con == null) return false;

        String sql = "INSERT INTO land_plots (land_alias, perimeter_m, area_m2) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, alias);
            ps.setDouble(2, perimeter);
            ps.setDouble(3, area);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error saving land record: " + e.getMessage());
            return false;
        }
    }

    // Optional method to query registered land records in the console (SELECT with ordering)
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
    }
}